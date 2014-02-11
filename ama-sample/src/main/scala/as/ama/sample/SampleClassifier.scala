package as.ama.sample

import akka.actor.ActorRef
import as.akka.broadcaster.Classifier

/**
 * This classifier will be used by broadcaster to test if we are interested (or not)
 * in this message.
 */
class SampleClassifier extends Classifier {
  override def map(message: Any, sender: ActorRef) = if (message == Sample.TestMessage || message.isInstanceOf[String]) Some(message) else None
}