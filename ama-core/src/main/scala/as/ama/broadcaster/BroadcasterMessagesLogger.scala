package as.ama.broadcaster

import akka.actor.{ ActorLogging, Actor }

object BroadcasterMessagesLogger {
  def classifier = new BroadcasterMessagesLoggerClassifier
}

class BroadcasterMessagesLogger extends Actor with ActorLogging {

  override def receive = {
    case message ⇒ log.debug(s"Message published on broadcaster: $message")
  }
}