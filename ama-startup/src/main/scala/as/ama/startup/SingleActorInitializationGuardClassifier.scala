package as.ama.startup

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef

class SingleActorInitializationGuardClassifier extends Classifier {
  override def map(message: Any, sender: ActorRef) = if (message.isInstanceOf[InitializationResult]) Some(message) else None
}