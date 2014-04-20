package as.ama.startup

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef
import as.ama.startup._

/**
 * List of messages that InitializationController is interested in.
 */
class InitializationControllerClassifier extends Classifier {

  override def map(message: Any, sender: ActorRef) = message match {
    case a: InitializationResult => Some(a)
    case a: StartupInitializer.AllActorsWereInstantiatedCorrectly => Some(a)
    case _ => None
  }
}
