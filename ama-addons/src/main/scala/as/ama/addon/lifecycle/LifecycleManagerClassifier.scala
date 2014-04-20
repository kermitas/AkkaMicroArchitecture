package as.ama.addon.lifecycle

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef

/**
 * List of messages that will go to LifecycleManager.
 */
class LifecycleManagerClassifier extends Classifier {
  override def map(message: Any, sender: ActorRef) = message match {
    case a: ShutdownSystem => Some(a)
    case _                 => None
  }
}
