package as.ama.addon

import akka.actor._
import as.jvm.JVMShutdownHook
import as.ama.addon.lifecycle._
import as.ama.startup.InitializationResult
import com.typesafe.config.Config

object InstallJVMShutdownHook {
  final val keepHookForTimeInMsConfigKey = "keepHookForTimeInMs"
}

/**
 * Install JVM shutdown hook.
 *
 * This actor is ready to be automatically initialized during ama startup. Should be defined on ama.initializeOnStartup.actors list
 * in application.conf, by default is defined in reference.conf (in ama-core project).
 *
 * @param commandLineArguments entered as arguments to program or defined in application.conf configuration file
 * @param config configuration defined in application.conf configuration file (for usage sample please see ama-sample project)
 * @param broadcaster main, pub-sub communication bus
 */
class InstallJVMShutdownHook(commandLineArguments: Array[String], config: Config, broadcaster: ActorRef, runtimeProperties: Map[String, Any]) extends Actor with ActorLogging {

  import InstallJVMShutdownHook._

  override def preStart() {
    try {
      val shutdownSystemMessage = new LifecycleListener.ShutdownSystem(Right("JVM shutdown hook was triggered"))
      val keepHookForTimeInMs = config.getInt(keepHookForTimeInMsConfigKey)
      val shutdownHook = new JVMShutdownHook(broadcaster, shutdownSystemMessage, keepHookForTimeInMs)

      Runtime.getRuntime.addShutdownHook(shutdownHook)
      log.debug("Installed JVM shutdown hook.")

      broadcaster ! new InitializationResult(Right(None))
    } catch {
      case e: Exception ⇒ broadcaster ! new InitializationResult(Left(new Exception("Problem while installing JVM shutdown hook.", e)))
    } finally {
      context.stop(self)
    }
  }

  override def postRestart(throwable: Throwable) = preStart()

  override def receive = {

    case message ⇒ log.warning(s"Unhandled $message send by ${sender()}")
  }

}
