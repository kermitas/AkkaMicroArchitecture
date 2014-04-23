package as.ama.startup

import akka.actor.{ ActorLogging, Actor, ActorRef, Props }

object StartupInitializer extends Serializable {

  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  sealed trait OutgoingMessage extends Message
  case class StartInitialization(commandLineArguments: Array[String], initializeOnStartupConfig: InitializeOnStartupConfig, broadcaster: ActorRef, amaConfigBuilder: AmaConfigBuilder) extends IncomingMessage with OutgoingMessage
  case class AllActorsWereInstantiatedCorrectly(startInitialization: StartInitialization) extends OutgoingMessage
  case class ProblemWhileInitializeActors(exception: Exception, startInitialization: StartInitialization) extends OutgoingMessage

  def classifier = new StartupInitializerClassifier
}

/**
 * Will create dedicated StartupInitializerWorker actor to handle request.
 */
class StartupInitializer extends Actor with ActorLogging {

  import StartupInitializer._

  override def receive = {

    case si: StartInitialization => {

      // parent of this actor should be an AmaRootActor
      val amaRootActor = context.parent

      context.actorOf(Props[StartupInitializerWorker]) ! new StartupInitializerWorker.StartInitialization(si, amaRootActor)
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }
}