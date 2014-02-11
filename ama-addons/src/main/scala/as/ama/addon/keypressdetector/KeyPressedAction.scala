package as.ama.addon.keypressdetector

import akka.actor.ActorRef
import as.io.ExecuteOnKeyPress
import as.ama.addon.lifecycle.LifecycleListener

object KeyPressedAction {
  sealed trait Message extends Serializable
  sealed trait OutgoingMessage extends Message
  case class KeyWasPressed(inputText: String) extends OutgoingMessage
}

/**
 * Instance of this class will be send in Init message to KeyPressedDetector.
 */
class KeyPressedAction(listener: ActorRef) extends ExecuteOnKeyPress {

  import KeyPressedAction._

  /**
   * This is a callback method executed by KeyPressDetector.
   *
   * When key will be pressed will send to listener actor:
   * - LifecycleListener.ShutdownSystem message when inputText was empty (and will finish work)
   * - KeyPressedAction.KeyWasPressed message with inputText (and will continue work)
   */
  override def keyWasPressedNotification(inputText: String): Boolean = {

    if (inputText.length == 0) {
      listener ! new LifecycleListener.ShutdownSystem(Right("Key was pressed."))
      false
    } else {
      listener ! new KeyWasPressed(inputText)
      true
    }
  }
}