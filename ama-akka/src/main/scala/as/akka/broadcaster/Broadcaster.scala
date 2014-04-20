package as.akka.broadcaster

import akka.actor._

object Broadcaster {
  final val forwarderName = "Forwarder"

  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  case class Register(listener: ActorRef, classifier: Classifier) extends IncomingMessage
  case class UnregisterClassifier(listener: ActorRef, classifier: Classifier) extends IncomingMessage
  case class Unregister(listener: ActorRef) extends IncomingMessage
}

/**
 * Publish-subscribe actor.
 *
 * To register please send Register message with classifier and listener where all classified messages will be forwarded.
 *
 * To unregister use Unregister or UnregisterClassifier messages.
 *
 * For more details please see ama-sample project.
 */
class Broadcaster extends Actor with ActorLogging {

  import Broadcaster._

  protected var forwarderCount: Int = _

  override def receive = {

    case register: Register => {
      val nextForwarderName = generateNextForwarderName(register.listener.path.name)
      //log.debug(s"Received message $register, creating forwarder with name '$nextForwarderName'")
      context.actorOf(Props[ClassifyingForwarder], nextForwarderName) ! register
    }

    case message => {
      //log.debug(s"Broadcasting message $message from ${sender()}")
      context.children.foreach(_.tell(message, sender()))
    }
  }

  protected def generateNextForwarderName(forwardeeName: String): String = {
    val s = s"${forwarderName}-$forwardeeName-$forwarderCount"
    forwarderCount += 1
    s
  }
}