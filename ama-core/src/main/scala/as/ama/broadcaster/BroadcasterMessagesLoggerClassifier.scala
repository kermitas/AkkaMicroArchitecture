package as.ama.broadcaster

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef

class BroadcasterMessagesLoggerClassifier extends Classifier {
  override def map(message: Any, sender: ActorRef) = Some(message)
}
