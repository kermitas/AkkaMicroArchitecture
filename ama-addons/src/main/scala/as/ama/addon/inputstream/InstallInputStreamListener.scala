package as.ama.addon.inputstream

import akka.actor.{ Actor, ActorLogging, Props }
import as.io.InputStreamListener
import as.ama.startup.AmaConfig
import as.ama.akka.CreateActorAndSendMessageExecuteInActorsContext

object InstallInputStreamListener {
  final val checkIfKeyWasPressedTimeIntervalInMsConfigKey = "checkIfKeyWasPressedTimeIntervalInMs"
}

/**
 * Install InputStreamListener.
 *
 * This actor is ready to be automatically initialized during ama startup. Should be defined on ama.initializeOnStartup.actors list
 * in application.conf, by default is defined in reference.conf (in ama-core project).
 */
class InstallInputStreamListener(amaConfig: AmaConfig) extends Actor with ActorLogging {

  import InstallInputStreamListener._

  /**
   * Will be executed when actor is created and also after actor restart (if postRestart() is not override).
   */
  override def preStart() {
    try {
      val inputStreamAction = new InputStreamListenerCallbackImpl(amaConfig.broadcaster)
      val checkIfKeyWasPressedTimeIntervalInMs = amaConfig.config.getInt(checkIfKeyWasPressedTimeIntervalInMsConfigKey)
      val initMessage = new InputStreamListener.Init(inputStreamAction, checkIfKeyWasPressedTimeIntervalInMs)

      context.parent ! new CreateActorAndSendMessageExecuteInActorsContext(Props[InputStreamListener], classOf[InputStreamListener].getSimpleName, initMessage, self)

      amaConfig.sendInitializationResult()
    } catch {
      case e: Exception => amaConfig.sendInitializationResult(new Exception("Problem while installing key press detector.", e))
    } finally {
      context.stop(self)
    }
  }

  override def receive = {
    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }
}