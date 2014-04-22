package as.ama.startup

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor._
import as.akka.util.CreateActorExecuteInActorsContext

object StartupInitializer extends Serializable {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  sealed trait OutgoingMessage extends Message
  case class InitialConfiguration(commandLineArguments: Array[String], initializeOnStartupConfig: InitializeOnStartupConfig, broadcaster: ActorRef, amaConfigBuilder: AmaConfigBuilder) extends IncomingMessage with OutgoingMessage
  case class AllActorsWereInstantiatedCorrectly(actorsCount: Int) extends OutgoingMessage
  case object GeneralInitializationTimeout extends IncomingMessage
  case object SingleActorInitializationTimeout extends IncomingMessage

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
  protected var numberOfCreatedActor = 0
  protected var generalCancellableTimeout: Cancellable = _
  protected var singleActorTimeout: Cancellable = _

  override def receive = {

    case initialConfiguration: InitialConfiguration => {
      this.initialConfiguration = initialConfiguration

      generalCancellableTimeout = context.system.scheduler.scheduleOnce(initialConfiguration.initializeOnStartupConfig.generalInitializationTimeoutInMs millis, self, GeneralInitializationTimeout)(context.dispatcher)

      log.debug("Initialization order:")
      for (initializeOnStartupActorConfig ← initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs) log.debug(s"${initializeOnStartupActorConfig.clazzName}, initializationOrder ${initializeOnStartupActorConfig.initializationOrder}")

      if (initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs.isEmpty) {
        positiveStop(initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs.size)
      } else {
        instantiateActor(numberOfCreatedActor)
      }
    }

    case GeneralInitializationTimeout => {
      val ite = new InitializationTimeoutException(s"General timeout (${initialConfiguration.initializeOnStartupConfig.generalInitializationTimeoutInMs} ms) while initializing actors on startup.")
      negativeStop(ite)
    }

    case SingleActorInitializationTimeout => {
      val ite = new InitializationTimeoutException(s"Timeout (${initialConfiguration.initializeOnStartupConfig.actorInitializationTimeoutInMs} ms) while initializing actor ${initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs(numberOfCreatedActor).clazzName}.")
      negativeStop(ite)
    }

    case ir: InitializationResult if ir.result.isLeft => stop

    case ir: InitializationResult if ir.result.isRight => {
      numberOfCreatedActor += 1

      if (numberOfCreatedActor < initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs.size) {
        instantiateActor(numberOfCreatedActor)
      } else {
        positiveStop(initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs.size)
      }
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }

  protected def positiveStop(numberOfCreatedActors: Int) {
    initialConfiguration.broadcaster ! new StartupInitializer.AllActorsWereInstantiatedCorrectly(numberOfCreatedActors)
    stop
  }

  protected def negativeStop(e: Exception) {
    initialConfiguration.broadcaster ! new InitializationResult(Left(e))
    stop
  }

  protected def stop {
    generalCancellableTimeout.cancel()
    if (singleActorTimeout != null) singleActorTimeout.cancel()
    context.stop(self)
  }

  protected def instantiateActor(index: Int) {
    val initializeOnStartupActorConfig = initialConfiguration.initializeOnStartupConfig.initializeOnStartupActorConfigs(index)
    instantiateActor(initialConfiguration.commandLineArguments, initializeOnStartupActorConfig, initialConfiguration.amaConfigBuilder, context.parent)
  }

  protected def instantiateActor(commandLineArguments: Array[String], initializeOnStartupActorConfig: InitializeOnStartupActorConfig, amaConfigBuilder: AmaConfigBuilder, amaRootActor: ActorRef) {
    try {
      if (singleActorTimeout != null) singleActorTimeout.cancel()

      val amaConfig = amaConfigBuilder.createAmaConfig(initializeOnStartupActorConfig.clazzName, commandLineArguments, initializeOnStartupActorConfig.config, initialConfiguration.broadcaster)

      val props = PropsCreator.createProps(initializeOnStartupActorConfig.clazzName, amaConfig)
      amaRootActor ! new CreateActorExecuteInActorsContext(props, initializeOnStartupActorConfig.clazzName)

      singleActorTimeout = context.system.scheduler.scheduleOnce(initialConfiguration.initializeOnStartupConfig.actorInitializationTimeoutInMs milliseconds, self, SingleActorInitializationTimeout)(context.dispatcher)
    } catch {
      case e: Exception => {
        log.error(e, s"Could not create actor ${initializeOnStartupActorConfig.clazzName}.")
        negativeStop(e)
      }
    }
  }
}