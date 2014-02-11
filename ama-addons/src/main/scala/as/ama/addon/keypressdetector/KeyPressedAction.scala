package as.ama.addon.keypressdetector

import akka.actor.ActorRef

class KeyPressedAction(listener: ActorRef, message: Any) extends Runnable with Serializable {
  override def run() = listener ! message
}