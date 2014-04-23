/*
package as.ama.startup

import akka.actor.ActorRef
import as.akka.broadcaster.Classifier

/**
 * List of messages that StartupInitializer is interested in.
 */
class StartupInitializerWorkerClassifier extends Classifier {

  override def map(message: Any, sender: ActorRef) = message match {
    case a: InitializationResult => Some(a)
    case _ => None
  }
}
*/ 