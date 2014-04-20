package as.ama.util

import akka.actor.{ ActorRef, FSM }
import as.ama.startup.InitializationResult

trait AmaFSMTerminate[S, D] extends Serializable { me: FSM[S, D] =>

  /**
   * When terminating in first state with failure then it will send unsuccessful InitializationResult on broadcaster.
   */
  protected def terminate(se: StopEvent, initialState: S, broadcaster: ActorRef) {
    se.reason match {
      case FSM.Normal   => log.info(s"Stopping (normal), state ${se.currentState}, data ${se.stateData}")
      case FSM.Shutdown => log.info(s"Stopping (shutdown), state ${se.currentState}, data ${se.stateData}")
      case FSM.Failure(cause) => {
        log.warning(s"Stopping (failure = $cause), state ${se.currentState}, data ${se.stateData}")

        if (se.currentState == initialState) {
          val e = new Exception(s"Problem while installing ${getClass.getSimpleName} actor.")
          if (cause.isInstanceOf[Exception]) e.initCause(cause.asInstanceOf[Exception])
          broadcaster ! new InitializationResult(Left(e))
        }
      }
    }
  }
}