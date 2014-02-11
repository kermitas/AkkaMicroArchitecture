package as.ama.broadcaster

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef

/**
 * BroadcasterMessagesLogger is interested to receive all messages published on broadcaster.
 */
class BroadcasterMessagesLoggerClassifier extends Classifier {
  override def map(message: Any, sender: ActorRef) = Some(message)
}
