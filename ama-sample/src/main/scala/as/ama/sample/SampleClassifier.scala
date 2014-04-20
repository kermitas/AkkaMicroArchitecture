package as.ama.sample

import akka.actor.ActorRef
import as.akka.broadcaster.Classifier
import as.ama.addon.inputstream.InputStreamListenerCallbackImpl

/**
 * This classifier will be used by broadcaster to test if we are interested (or not)
 * in this message.
 */
class SampleClassifier extends Classifier {
  override def map(message: Any, sender: ActorRef) = message match {
    case Sample.TestMessage => Some(message)
    case a: String => Some(a)
    case a: InputStreamListenerCallbackImpl.InputStreamText => Some(a)
    case _ => None
  }
}