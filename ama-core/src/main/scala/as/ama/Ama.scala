package as.ama

import _root_.akka.actor._

object Ama {

  protected var ama: ActorRef = _

  def apply(actorSystem: ActorSystem): ActorRef = synchronized {
    if (ama == null) ama = actorSystem.actorOf(Props[Ama], name = "Ama")
    ama
  }

  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  sealed trait OutgoingMessage extends Message
  case class CreateNewAmaRootActor(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds: Int) extends IncomingMessage
  case class CreatedAmaRootActor(amaRootActor: ActorRef, cnara: CreateNewAmaRootActor) extends OutgoingMessage

}

class Ama extends Actor with ActorLogging {

  import Ama._

  protected var amaRootActorNumber = -1

  override def receive = {

    case cnara @ CreateNewAmaRootActor(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds) => {
      amaRootActorNumber += 1
      val amaRootActor = context.actorOf(Props[AmaRootActor], name = classOf[AmaRootActor].getSimpleName + "-" + amaRootActorNumber)
      amaRootActor ! new AmaRootActor.AutomaticDieTimeWhenSystemNotCreated(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds)
      sender() ! new CreatedAmaRootActor(amaRootActor, cnara)
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }
}