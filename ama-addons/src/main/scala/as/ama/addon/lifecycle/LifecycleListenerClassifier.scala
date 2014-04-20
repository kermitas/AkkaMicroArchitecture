package as.ama.addon.lifecycle

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef

/**
 * Defines that LifecycleListener is interested only in LifecycleListener.ShutdownSystem message.
 */
class LifecycleListenerClassifier extends Classifier {
  override def map(message: Any, sender: ActorRef) = message match {
    case a: LifecycleListener.ShutdownSystem => Some(a)
    case _ => None
  }
}
