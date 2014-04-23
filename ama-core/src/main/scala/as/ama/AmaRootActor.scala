package as.ama

import scala.language.postfixOps
import scala.concurrent.duration._
import com.typesafe.config.Config
import _root_.akka.actor._
import as.akka.broadcaster._
import as.ama.startup._
import as.ama.broadcaster.BroadcasterMessagesLogger
import as.ama.akka.ExecuteInActorsContext

object AmaRootActor {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  sealed trait OutgoingMessage extends Message
  case class AutomaticDieTimeWhenSystemNotCreated(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds: Int) extends IncomingMessage
  case object CreateBroadcaster extends IncomingMessage
  case class CreatedBroadcaster(broadcaster: ActorRef) extends OutgoingMessage

  case class Init(broadcaster: ActorRef, config: Config, originallyPassedCommandLineArguments: Array[String]) extends IncomingMessage
  case class InitializationResult(exception: Option[Exception]) extends OutgoingMessage

  sealed trait InternalIncomingMessage extends IncomingMessage
  case object SystemNotCreatedTimeout extends InternalIncomingMessage

  final val commandLineArgumentsRegex = "\"(\\\"|[^\"])*?\"|[^\\s]+"
}

// TODO should be a FSM actor!
/**
 * AmaRootActor is root actor for whole ama (created by Init message).
 */

class AmaRootActor extends Actor with ActorLogging {

  import AmaRootActor._

  protected var inactivityTimeout: Option[Cancellable] = None

  override def receive = {

    case AutomaticDieTimeWhenSystemNotCreated(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds) => {
      import context.dispatcher
      inactivityTimeout = Some(context.system.scheduler.scheduleOnce(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds seconds, self, SystemNotCreatedTimeout))
    }

    case SystemNotCreatedTimeout => context.stop(self)

    case CreateBroadcaster => {
      val broadcaster = context.actorOf(Props[Broadcaster], classOf[Broadcaster].getSimpleName)
      sender() ! new CreatedBroadcaster(broadcaster)
    }

    case Init(broadcaster, config, originallyPassedCommandLineArguments) => {
      try {
        initialize(broadcaster, config, originallyPassedCommandLineArguments)

        inactivityTimeout map { cancellable =>
          cancellable.cancel()
          inactivityTimeout = None
        }

        sender() ! new InitializationResult(None)
      } catch {
        case e: Exception => {
          log.error(s"Problem while initializing ${getClass.getSimpleName}.", e)
          sender() ! new InitializationResult(Some(e))
          context.stop(self)
        }
      }
    }

    case eiac: ExecuteInActorsContext => eiac.execute(context)

    case message                      => log.warning(s"Unhandled $message send by ${sender()}")
  }

  protected def initialize(broadcaster: ActorRef, config: Config, originallyPassedCommandLineArguments: Array[String]) {

    if (config.hasPath(AmaConfig.renderConfigurationConfigKey) && config.getBoolean(AmaConfig.renderConfigurationConfigKey)) log.info(s"Configuration: ${config.root.render}")

    val amaConfig = AmaConfig(config)

    val cmdArgs = prepareCommandLineArguments(originallyPassedCommandLineArguments, amaConfig.commandLineConfig)

    log.debug(s"Command line arguments count ${cmdArgs.length}: ${cmdArgs.mkString(",")}")

    val amaConfigBuilder: AmaConfigBuilder = Class.forName(amaConfig.initializeOnStartupConfig.amaConfigBuilderClassName).getConstructor().newInstance().asInstanceOf[AmaConfigBuilder]

    if (amaConfig.logMessagesPublishedOnBroadcaster) {
      val broadcasterMessagesLogger = context.actorOf(Props[BroadcasterMessagesLogger], classOf[BroadcasterMessagesLogger].getSimpleName)
      broadcaster ! new Broadcaster.Register(broadcasterMessagesLogger, BroadcasterMessagesLogger.classifier)
    }

    val initializationController = context.actorOf(InitializationController.props(broadcaster), classOf[InitializationController].getSimpleName)
    broadcaster ! new Broadcaster.Register(initializationController, InitializationController.classifier)

    val startupInitializer = context.actorOf(Props[StartupInitializer], classOf[StartupInitializer].getSimpleName)
    broadcaster ! new Broadcaster.Register(startupInitializer, StartupInitializer.classifier)

    broadcaster ! new StartupInitializer.StartInitialization(cmdArgs, amaConfig.initializeOnStartupConfig, broadcaster, amaConfigBuilder)
  }

  protected def prepareCommandLineArguments(originallyPassedCommandLineArguments: Array[String], commandLineConfig: CommandLineConfig): Array[String] = {
    if (commandLineConfig.overrideOriginallyPassedArguments) {
      log.info(s"Overwriting command line arguments with '${commandLineConfig.arguments}'")
      new scala.util.matching.Regex(commandLineArgumentsRegex).findAllIn(commandLineConfig.arguments.trim).toArray
    } else
      originallyPassedCommandLineArguments
  }
}