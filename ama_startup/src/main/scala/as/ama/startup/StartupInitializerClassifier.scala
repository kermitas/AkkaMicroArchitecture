package as.ama.startup

import akka.actor.ActorRef
import as.akka.broadcaster.Classifier

/**
 * List of messages that StartupInitializer is interested in.
 */
class StartupInitializerClassifier extends Classifier {

  override def map(message: Any, sender: ActorRef) = message match {
    case ic: StartupInitializer.InitialConfiguration ⇒ Some(ic)
    case pi: StartupInitializer.PleaseInstantiate ⇒ Some(pi)
    case ir: InitializationResult if ir.result.isRight ⇒ Some(ir)
    case _ ⇒ None
  }
}