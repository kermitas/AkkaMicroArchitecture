package as.ama.startup

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef

/**
 * List of messages that InitializationController is interested in.
 */
class InitializationControllerClassifier extends Classifier {

  override def map(message: Any, sender: ActorRef) = message match {
    case a: StartupInitializer.AllActorsWereInstantiatedCorrectly => Some(a)
    case a: StartupInitializer.ProblemWhileInitializeActors => Some(a)
    case _ => None
  }
}
