package as.ama.startup

import akka.actor._
import as.ama.addon.lifecycle._
import as.ama.startup._

object InitializationController {
  def classifier = new InitializationControllerClassifier

  def props(broadcaster: ActorRef) = Props(new InitializationController(broadcaster))
}

/**
 * Main role is to shutdown system if any of automatically initialized actors will fail.
 */
class InitializationController(broadcaster: ActorRef) extends Actor with ActorLogging {

  override def receive = {

    case initializationResult: InitializationResult ⇒ {
      log.error("Will shut down system because one of automatically stated actors (during startup) failed.")
      val e = new Exception("Shutting down system because of problem while startup initialization of one of actors.", initializationResult.result.left.get)
      broadcaster ! new LifecycleListener.ShutdownSystem(Left(e))
      context.stop(self)
    }

    case StartupInitializer.AllActorsWereInstantiatedCorrectly(actorsCount) ⇒ context.stop(self)

    case message ⇒ log.warning(s"Unhandled $message send by ${sender()}")
  }
}