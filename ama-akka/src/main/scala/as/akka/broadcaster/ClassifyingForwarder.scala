package as.akka.broadcaster

import akka.actor._

/**
 * For each subscriber instance of ClassifyingForwarder actor will be created.
 *
 * It is responsible to ask classifier and if message is accepted then it forwards it.
 */
class ClassifyingForwarder extends Actor with ActorLogging {

  import Broadcaster._

  protected var listener: ActorRef = _
  protected var classifier: Classifier = _

  override def receive = {

    case Register(listener, classifier) ⇒ {
      //log.debug(s"Registering forwarder to $listener with classifier $classifier")
      this.listener = listener
      this.classifier = classifier
      context.watch(listener)
    }

    case UnregisterClassifier(listener, classifier) ⇒ if (this.listener == listener && this.classifier == classifier) {
      //log.debug(s"Unregister request, forwarding to $listener using classifier $classifier finished")
      context.stop(self)
    }

    case Unregister(listener) ⇒ if (this.listener == listener) {
      //log.debug(s"Unregister request, forwarding to $listener finished")
      context.stop(self)
    }

    case Terminated(diedActor) ⇒ {
      //log.debug(s"Forwardee (actor that I am forwarding to, $listener) died, finishing")
      context.stop(self)
    }

    case messageToForward: Any ⇒ classifier.map(messageToForward, sender()).foreach(listener.tell(_, sender()))
  }
}
