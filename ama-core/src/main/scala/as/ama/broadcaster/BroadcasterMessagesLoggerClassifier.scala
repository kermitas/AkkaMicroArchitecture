package as.ama.broadcaster

import as.akka.broadcaster.Classifier
import akka.util.MessageWithSender

/**
 * BroadcasterMessagesLogger is interested to receive all messages published on broadcaster.
 */
class BroadcasterMessagesLoggerClassifier extends Classifier {
  override def map(messageWithSender: MessageWithSender[Any]) = Some(messageWithSender)
}
