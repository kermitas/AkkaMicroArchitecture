package as.ama.sample

import as.akka.broadcaster.{ MessageWithSender, Classifier }
import as.ama.addon.inputstream.InputStreamText

/**
 * This classifier will be used by broadcaster to test if we are interested (or not)
 * in this message.
 */
class SampleClassifier extends Classifier {
  override def map(messageWithSender: MessageWithSender[Any]) = messageWithSender.message match {
    case Sample.TestMessage => Some(messageWithSender)
    case a: String          => Some(messageWithSender)
    case a: InputStreamText => Some(messageWithSender)
    case _                  => None
  }
}