package as.ama.addon.lifecycle

import as.akka.broadcaster.{ MessageWithSender, Classifier }

/**
 * List of messages that will go to LifecycleManager.
 */
class LifecycleManagerClassifier extends Classifier {
  override def map(messageWithSender: MessageWithSender[Any]) = messageWithSender.message match {
    case a: ShutdownSystem => Some(messageWithSender)
    case _                 => None
  }
}
