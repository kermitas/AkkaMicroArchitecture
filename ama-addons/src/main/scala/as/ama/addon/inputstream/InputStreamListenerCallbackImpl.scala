package as.ama.addon.inputstream

import akka.actor.ActorRef
import as.io.InputStreamListenerCallback

/**
 * Instance of this class will be send in Init message to InputStreamListener.
 */
class InputStreamListenerCallbackImpl(listener: ActorRef) extends InputStreamListenerCallback {

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