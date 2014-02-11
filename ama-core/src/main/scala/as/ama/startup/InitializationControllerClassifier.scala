package as.ama.startup

import as.akka.broadcaster.Classifier
import akka.actor.ActorRef
import as.ama.startup._

class InitializationControllerClassifier extends Classifier {

  override def map(message: Any, sender: ActorRef) = message match {
    case ir: InitializationResult if ir.result.isLeft ⇒ Some(ir)
    case aawic: StartupInitializer.AllActorsWereInstantiatedCorrectly ⇒ Some(aawic)
    case _ ⇒ None
  }
}
