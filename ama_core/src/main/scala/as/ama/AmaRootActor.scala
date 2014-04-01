package as.ama

import akka.actor._
import as.akka.broadcaster._
import as.ama.startup._
import as.ama.broadcaster.BroadcasterMessagesLogger
import as.akka.util.ExecuteInActorsContext

object AmaRootActor {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  case class Init(amaConfig: AmaConfig, commandLineArguments: Array[String], runtimePropertiesBuilder: RuntimePropertiesBuilder) extends IncomingMessage
}

/**
 * MainActor (similar to main class on JVM).
 *
 * Responsibilities:
 * - start and register BroadcasterMessagesLogger if needed
 * - start and register InitializationController (that will shutdown system when any of automatically initialized actors will fail to initialize)
 * - start and register StartupInitializer (that will read configuration and initialize acotrs defined there)
 */
class AmaRootActor extends Actor with ActorLogging {

  import AmaRootActor._

  override def receive = {

    case Init(amaConfig, commandLineArguments, runtimePropertiesBuilder) => {
      try {
        initialize(amaConfig, commandLineArguments, runtimePropertiesBuilder)
      } catch {
        case e: Exception => {
          log.error(s"Problem while initializing ${classOf[Broadcaster].getSimpleName} and/or ${classOf[StartupInitializer].getSimpleName}.", e)
          context.system.shutdown()
        }
      }
    }

    case eiac: ExecuteInActorsContext => eiac.execute(context)

    case message                      => log.warning(s"Unhandled $message send by ${sender()}")
  }

  protected def initialize(amaConfig: AmaConfig, commandLineArguments: Array[String], runtimePropertiesBuilder: RuntimePropertiesBuilder) {
    log.debug(s"Command line arguments count ${commandLineArguments.length}: ${commandLineArguments.mkString(",")}")

    val broadcaster = context.actorOf(Props[Broadcaster], classOf[Broadcaster].getSimpleName)

    if (amaConfig.logMessagesPublishedOnBroadcaster) {
      val broadcasterMessagesLogger = context.actorOf(Props[BroadcasterMessagesLogger], classOf[BroadcasterMessagesLogger].getSimpleName)
      broadcaster ! new Broadcaster.Register(broadcasterMessagesLogger, BroadcasterMessagesLogger.classifier)
    }

    val initializationController = context.actorOf(InitializationController.props(broadcaster), classOf[InitializationController].getSimpleName)
    broadcaster ! new Broadcaster.Register(initializationController, InitializationController.classifier)

    val startupInitializer = context.actorOf(Props[StartupInitializer], classOf[StartupInitializer].getSimpleName)
    broadcaster ! new Broadcaster.Register(startupInitializer, StartupInitializer.classifier)

    broadcaster ! new StartupInitializer.InitialConfiguration(commandLineArguments, amaConfig.initializeOnStartupConfig, broadcaster, runtimePropertiesBuilder)
  }
}