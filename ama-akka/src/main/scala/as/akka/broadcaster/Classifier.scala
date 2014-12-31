package as.akka.broadcaster

import akka.actor._

/**
 * Extends this class to define your classifier.
 *
 * By accepting message - returning Some(message_or_whatever_you_want) - it will be delivered to your actor.
 *
 * Please see ama-sample project for more details.
 */
trait Classifier extends Serializable {
  def map(messageWithSender: MessageWithSender[Any]): Option[MessageWithSender[Any]]
}
