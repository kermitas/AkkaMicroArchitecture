package as.ama.startup

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor._
import as.akka.broadcaster._

object SingleActorInitializationGuard {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  sealed trait OutgoingMessage extends Message
  case object InitializationTimeout extends IncomingMessage
}

/**
 * Will take care of single actor initialization.
 *
 * Correctly initialized actor should publish InitializationResult(Right(...)) on broadcaster.
 *
 * Incorrectly initialized actor should publish InitializationResult(Left(exception)) on broadcaster.
 * If it will not publish timeout will be reached.
 */
class SingleActorInitializationGuard extends Actor with ActorLogging {

  import SingleActorInitializationGuard._

  protected var broadcaster: ActorRef = _
  protected var commandLineArguments: Array[String] = _
  protected var cancellableTimeout: Cancellable = _
  protected var initializeOnStartupActorConfig: InitializeOnStartupActorConfig = _
  protected var initializeOnStartupConfig: InitializeOnStartupConfig = _
  protected var createdActor: ActorRef = _
  protected var runtimePropertiesBuilder: RuntimePropertiesBuilder = _

  override def receive = {
    case (broadcaster: ActorRef, commandLineArguments: Array[String], initializeOnStartupActorConfig: InitializeOnStartupActorConfig, initializeOnStartupConfig: InitializeOnStartupConfig, runtimePropertiesBuilder: RuntimePropertiesBuilder) ⇒ { //}, initialConfiguration: StartupInitializer.InitialConfiguration) ⇒ {
      this.broadcaster = broadcaster
      this.commandLineArguments = commandLineArguments
      this.initializeOnStartupActorConfig = initializeOnStartupActorConfig
      this.initializeOnStartupConfig = initializeOnStartupConfig
      this.runtimePropertiesBuilder = runtimePropertiesBuilder

      broadcaster ! new Broadcaster.Register(self, new SingleActorInitializationGuardClassifier)

      instantiateActor
    }

    case initializationResult: InitializationResult ⇒ {
      if (sender().path.name.equals(initializeOnStartupActorConfig.clazzName)) {
        cancellableTimeout.cancel
        context.stop(self)
      }
    }

    case InitializationTimeout ⇒ {
      val ite = new InitializationTimeoutException(s"Timeout (${initializeOnStartupConfig.actorInitializationTimeoutInMs} ms) while waiting for InitializationResult from ${initializeOnStartupActorConfig.clazzName}.")
      broadcaster ! new InitializationResult(Left(ite))
      context.stop(self)
    }
  }

  protected def instantiateActor {
    try {
      val runtimeProperties = runtimePropertiesBuilder.createRuntimeProperties(initializeOnStartupActorConfig.clazzName, commandLineArguments, initializeOnStartupActorConfig.config, broadcaster)

      createdActor = context.system.actorOf(new PropsCreator(initializeOnStartupActorConfig.clazzName, commandLineArguments, initializeOnStartupActorConfig.config, broadcaster, runtimeProperties).create, initializeOnStartupActorConfig.clazzName)

      cancellableTimeout = context.system.scheduler.scheduleOnce(initializeOnStartupConfig.actorInitializationTimeoutInMs milliseconds, self, InitializationTimeout)(context.dispatcher)
    } catch {
      case e: Exception ⇒ {
        log.error(e, s"Could not create actor ${initializeOnStartupActorConfig.clazzName}")
        broadcaster ! new InitializationResult(Left(e))
        context.stop(self)
      }
    }
  }
}