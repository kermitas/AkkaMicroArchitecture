package as.ama.startup

import as.akka.broadcaster.{ Classifier, MessageWithSender }

/**
 * List of messages that StartupInitializer is interested in.
 */
class StartupInitializerClassifier extends Classifier {

  override def map(messageWithSender: MessageWithSender[Any]) = messageWithSender.message match {
    case a: StartupInitializer.StartInitialization => Some(messageWithSender)
    case _                                         => None
  }
}