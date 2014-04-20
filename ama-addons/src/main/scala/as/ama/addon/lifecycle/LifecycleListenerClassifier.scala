package as.ama.addon.lifecycle

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef

/**
 * Defines that LifecycleListener is interested only in LifecycleListener.ShutdownSystem message.
 */
class LifecycleListenerClassifier extends Classifier {
  override def map(message: Any, sender: ActorRef) = if (message.isInstanceOf[LifecycleListener.ShutdownSystem]) Some(message) else None
}
