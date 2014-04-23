package as.ama.startup

import akka.actor._
import as.ama.addon.lifecycle.ShutdownSystem

object InitializationController {
  def classifier = new InitializationControllerClassifier
  def props(broadcaster: ActorRef) = Props(new InitializationController(broadcaster))
}

/**
 * Main role is to shutdown system if any of automatically initialized actors will fail.
 */
class InitializationController(broadcaster: ActorRef) extends Actor with ActorLogging {

  override def receive = {

    case StartupInitializer.AllActorsWereInstantiatedCorrectly(actorsCount) => context.stop(self)

    case StartupInitializer.ProblemWhileInitializeActors(exception, initialConfiguration) => {
      val description = "Publishing ShutdownSystem message because of problem while startup initialization of one of actors."
      log.error(description)
      val e = new Exception(description, exception)
      broadcaster ! new ShutdownSystem(Left(e))
      context.stop(self)
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }
}