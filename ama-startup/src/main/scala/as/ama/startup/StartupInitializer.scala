package as.ama.startup

import akka.actor.{ ActorLogging, Actor, ActorRef, Props }

object StartupInitializer extends Serializable {

  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  sealed trait OutgoingMessage extends Message
  case class InitialConfiguration(commandLineArguments: Array[String], initializeOnStartupConfig: InitializeOnStartupConfig, broadcaster: ActorRef, amaConfigBuilder: AmaConfigBuilder) extends IncomingMessage with OutgoingMessage
  case class AllActorsWereInstantiatedCorrectly(initialConfiguration: InitialConfiguration) extends OutgoingMessage
  case class ProblemWhileInitializeActors(exception: Exception, initialConfiguration: InitialConfiguration) extends OutgoingMessage

  def classifier = new StartupInitializerClassifier
}

/**
 * Will create dedicated StartupInitializerWorker actor to handle request.
 */
class StartupInitializer extends Actor with ActorLogging {

  import StartupInitializer._

  override def receive = {

    case ic: InitialConfiguration => {
      // parent of this actor should be an AmaRootActor
      context.actorOf(Props[StartupInitializerWorker]) ! new StartupInitializerWorker.InitialConfiguration(ic, context.parent)
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }
}