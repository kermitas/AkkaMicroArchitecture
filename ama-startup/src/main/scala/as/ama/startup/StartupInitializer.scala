package as.ama.startup

import scala.language.postfixOps
import akka.actor._
import scala.concurrent.duration._

object StartupInitializer extends Serializable {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  sealed trait OutgoingMessage extends Message
  case class InitialConfiguration(commandLineArguments: Array[String], initializeOnStartupConfig: InitializeOnStartupConfig, broadcaster: ActorRef, amaConfigBuilder: AmaConfigBuilder) extends IncomingMessage with OutgoingMessage
  case class PleaseInstantiate(initializeOnStartupActorConfig: InitializeOnStartupActorConfig, broadcaster: ActorRef) extends IncomingMessage with OutgoingMessage
  case class AllActorsWereInstantiatedCorrectly(actorsCount: Int) extends OutgoingMessage
  case object InitializationTimeout extends IncomingMessage

  def classifier = new StartupInitializerClassifier
}

/**
 * Will instantiate actors defined on ama.initializeOnStartup.actors list (in reference.conf or application.conf).
 *
 * Please see documentation folder for diagrams with detailed message flows.
 *
 * Please see ama-sample for basic usage.
 */
class StartupInitializer extends Actor with ActorLogging {

  import StartupInitializer._

  protected var initialConfiguration: InitialConfiguration = _
  protected var numberOfCreatedGuardians = 0
  protected var numberOfCreatedSuccessfullyCreatedActors = 0
  protected var cancellableTimeout: Cancellable = _

  override def receive = {

    case initialConfiguration: InitialConfiguration => {
      this.initialConfiguration = initialConfiguration

      cancellableTimeout = context.system.scheduler.scheduleOnce(initialConfiguration.initializeOnStartupConfig.generalInitializationTimeoutInMs millis, self, InitializationTimeout)(context.dispatcher)

      log.debug("Initialization order:")
      for (initializeOnStartupActorConfig ← initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs) log.debug(s"${initializeOnStartupActorConfig.clazzName}, initializationOrder ${initializeOnStartupActorConfig.initializationOrder}")

      initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs.foreach(initialConfiguration.broadcaster ! new PleaseInstantiate(_, initialConfiguration.broadcaster))
    }

    case PleaseInstantiate(initializeOnStartupActorConfig, broadcaster) => {
      numberOfCreatedGuardians += 1
      val initializationGuard = context.actorOf(Props[SingleActorInitializationGuard], classOf[SingleActorInitializationGuard].getSimpleName + "-" + initializeOnStartupActorConfig.clazzName + "-" + numberOfCreatedGuardians)
      initializationGuard ! (broadcaster, initialConfiguration.commandLineArguments, initializeOnStartupActorConfig, initialConfiguration.initializeOnStartupConfig, initialConfiguration.amaConfigBuilder, context.parent)
    }

    case InitializationTimeout => {
      val ite = new InitializationTimeoutException(s"General timeout (${initialConfiguration.initializeOnStartupConfig.generalInitializationTimeoutInMs} ms) while initializing actors on startup.")
      initialConfiguration.broadcaster ! new InitializationResult(Left(ite))
      context.stop(self)
    }

    case initializationResult: InitializationResult => {
      numberOfCreatedSuccessfullyCreatedActors += 1
      if (numberOfCreatedGuardians == numberOfCreatedSuccessfullyCreatedActors) {
        cancellableTimeout.cancel()
        initialConfiguration.broadcaster ! new StartupInitializer.AllActorsWereInstantiatedCorrectly(numberOfCreatedGuardians)
        context.stop(self)
      }
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }
}