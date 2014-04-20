package as.ama.startup

import akka.actor.ActorRef
import as.akka.broadcaster.Classifier

/**
 * List of messages that StartupInitializer is interested in.
 */
class StartupInitializerClassifier extends Classifier {

  override def map(message: Any, sender: ActorRef) = message match {
    case a: StartupInitializer.InitialConfiguration => Some(a)
    case a: StartupInitializer.PleaseInstantiate => Some(a)
    case a: InitializationResult => Some(a)
    case _ => None
  }
}