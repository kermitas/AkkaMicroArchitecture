package as.ama.addon.inputstream

import akka.actor.ActorRef
import as.io.InputStreamListenerCallback
import as.ama.addon.lifecycle.LifecycleManager

object InputStreamListenerCallbackImpl {
  sealed trait Message extends Serializable
  sealed trait OutgoingMessage extends Message
  case class InputStreamText(inputText: String) extends OutgoingMessage
}

/**
 * Instance of this class will be send in Init message to InputStreamListener.
 */
class InputStreamListenerCallbackImpl(listener: ActorRef) extends InputStreamListenerCallback {

  import InputStreamListenerCallbackImpl._

  /**
   * This is a callback method executed by InputStreamListener.
   *
   * When key will be pressed will send to listener actor InputStreamListenerCallbackImpl.InputStreamText message with inputText
   * (and will continue to listen for next input).
   */
  override def inputStreamNotification(inputText: String): Boolean = {
    listener ! new InputStreamText(inputText)
    true
  }
}