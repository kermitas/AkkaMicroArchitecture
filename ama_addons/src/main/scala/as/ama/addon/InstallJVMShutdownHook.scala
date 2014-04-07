package as.ama.addon

import akka.actor._
import as.jvm.JVMShutdownHook
import as.ama.addon.lifecycle._
import as.ama.startup.InitializationResult
import as.ama.startup.AmaConfig

object InstallJVMShutdownHook {
  final val keepHookForTimeInMsConfigKey = "keepHookForTimeInMs"
}

/**
 * Install JVM shutdown hook.
 *
 * This actor is ready to be automatically initialized during ama startup. Should be defined on ama.initializeOnStartup.actors list
 * in application.conf, by default is defined in reference.conf (in ama-core project).
 */
class InstallJVMShutdownHook(amaConfig: AmaConfig) extends Actor with ActorLogging {

  import InstallJVMShutdownHook._

  override def preStart() {
    try {
      val shutdownSystemMessage = new LifecycleListener.ShutdownSystem(Right("JVM shutdown hook was triggered"))
      val keepHookForTimeInMs = amaConfig.config.getInt(keepHookForTimeInMsConfigKey)
      val shutdownHook = new JVMShutdownHook(amaConfig.broadcaster, shutdownSystemMessage, keepHookForTimeInMs)

      Runtime.getRuntime.addShutdownHook(shutdownHook)
      log.debug("Installed JVM shutdown hook.")

      amaConfig.broadcaster ! new InitializationResult(Right(None))
    } catch {
      case e: Exception => amaConfig.broadcaster ! new InitializationResult(Left(new Exception("Problem while installing JVM shutdown hook.", e)))
    } finally {
      context.stop(self)
    }
  }

  override def postRestart(throwable: Throwable) = preStart()

  override def receive = {

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }

}
