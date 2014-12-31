package as.ama.startup

import as.akka.broadcaster.{ MessageWithSender, Classifier }

/**
 * List of messages that InitializationController is interested in.
 */
class InitializationControllerClassifier extends Classifier {

  override def map(messageWithSender: MessageWithSender[Any]) = messageWithSender.message match {
    case a: StartupInitializer.AllActorsWereInstantiatedCorrectly => Some(messageWithSender)
    case a: StartupInitializer.ProblemWhileInitializeActors => Some(messageWithSender)
    case _ => None
  }
}
