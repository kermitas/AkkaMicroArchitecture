package as.ama

import akka.actor._
import as.akka.broadcaster._
import as.ama.startup._
import as.ama.broadcaster.BroadcasterMessagesLogger

object MainActor {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  case class Init(amaConfig: AmaConfig, commandLineArguments: Array[String]) extends IncomingMessage
}

/**
 * MainActor (simillar to main class on JVM).
 *
 * Resposibilities:
 * - start and register BroadcasterMessagesLogger if needed
 * - start and register InitializationController (that will shutdown system when any of automatically initialized actors will fail to initialize)
 * - start and register StartupInitializer (that will read configuration and initialize acotrs defined there)
 */
class MainActor extends Actor with ActorLogging {

  import MainActor._

  override def receive = {

    case Init(amaConfig, commandLineArguments) ⇒ {
      try {
        initialize(amaConfig, commandLineArguments)
      } catch {
        case e: Exception ⇒ {
          log.error(s"Problem while initializing ${classOf[Broadcaster].getSimpleName} and/or ${classOf[StartupInitializer].getSimpleName}.", e)
          context.system.shutdown()
        }
      } finally {
        context.stop(self)
      }
    }

    case message ⇒ log.warning(s"Unhandled $message send by ${sender()}")
  }

  protected def initialize(amaConfig: AmaConfig, commandLineArguments: Array[String]) {
    log.debug(s"Command line arguments count ${commandLineArguments.length}: ${commandLineArguments.mkString(",")}")

    val broadcaster = context.system.actorOf(Props[Broadcaster], classOf[Broadcaster].getSimpleName)

    if (amaConfig.logMessagesPublishedOnBroadcaster) {
      val broadcasterMessagesLogger = context.system.actorOf(Props[BroadcasterMessagesLogger], classOf[BroadcasterMessagesLogger].getSimpleName)
      broadcaster ! new Broadcaster.Register(broadcasterMessagesLogger, BroadcasterMessagesLogger.classifier)
    }

    val initializationController = context.system.actorOf(InitializationController.props(broadcaster), classOf[InitializationController].getSimpleName)
    broadcaster ! new Broadcaster.Register(initializationController, InitializationController.classifier)

    val startupInitializer = context.system.actorOf(Props[StartupInitializer], classOf[StartupInitializer].getSimpleName)
    broadcaster ! new Broadcaster.Register(startupInitializer, StartupInitializer.classifier)

    broadcaster ! new StartupInitializer.InitialConfiguration(commandLineArguments, amaConfig.initializeOnStartupConfig, broadcaster)
  }
}

