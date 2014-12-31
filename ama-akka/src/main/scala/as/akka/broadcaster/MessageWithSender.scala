package as.akka.broadcaster

import akka.actor.ActorRef

case class MessageWithSender[T](message: T, messageSender: ActorRef) extends Serializable