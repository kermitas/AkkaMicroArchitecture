package as.ama

import _root_.akka.actor._
import java.math.BigInteger

object Ama {

  protected var amaRootActorNumber = new BigInteger("-1")
  protected var ama: ActorRef = _

  def apply(actorSystem: ActorSystem): ActorRef = synchronized {
    if (ama == null) ama = actorSystem.actorOf(Props[Ama], name = "Ama")
    ama
  }

  // TODO: should be changed into something like this
  /*
  def apply(actorSystem: ActorSystem, timeoutInMilliseconds: Int): ActorRef = {
    try {
      actorSystem.actorOf(Props[CompilationManager], name = classOf[CompilationManager].getSimpleName)
    } catch {
      case e: InvalidActorNameException => {
        val actorSelection = actorSystem.actorSelection("/user/" + classOf[CompilationManager].getSimpleName)
        val future: Future[ActorRef] = actorSelection.resolveOne(timeoutInMilliseconds milliseconds)
        Await.result(future, timeoutInMilliseconds milliseconds)
      }
    }
  }
  */

  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  sealed trait OutgoingMessage extends Message
  case class CreateNewAmaRootActor(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds: Int) extends IncomingMessage
  case class CreatedAmaRootActor(amaRootActor: ActorRef, cnara: CreateNewAmaRootActor) extends OutgoingMessage

  def getNextNameOfAmaRootActor: String = {
    amaRootActorNumber = amaRootActorNumber.add(BigInteger.ONE)
    classOf[AmaRootActor].getSimpleName + "-" + amaRootActorNumber.toString
  }
}

class Ama extends Actor with ActorLogging {

  import Ama._

  override def receive = {

    case cnara @ CreateNewAmaRootActor(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds) => {
      val amaRootActor = context.actorOf(Props[AmaRootActor], name = getNextNameOfAmaRootActor)
      amaRootActor ! new AmaRootActor.AutomaticDieTimeWhenSystemNotCreated(amaRootActorAutomaticDieIfAmaSystemNotCreatedInSeconds)
      sender() ! new CreatedAmaRootActor(amaRootActor, cnara)
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }
}