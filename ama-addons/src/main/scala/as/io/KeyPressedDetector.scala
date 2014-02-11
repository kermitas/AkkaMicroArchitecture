package as.io

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor._

object KeyPressedDetector {
  sealed trait State
  case object Uninitialized extends State
  case object WaitingForAKey extends State

  sealed trait StateData
  case object UninitializedStateData extends StateData
  case class WaitingForAKeyStateData(runnableToExecuteOnKeyPress: Runnable) extends StateData

  sealed trait Messages extends Serializable
  sealed trait IncomingMessages extends Messages
  case class Init(runnableToExecuteOnKeyPress: Runnable, checkIfKeyWasPressedTimeIntervalInMs: Int) extends IncomingMessages
}

class KeyPressedDetector extends Actor with FSM[KeyPressedDetector.State, KeyPressedDetector.StateData] {

  import KeyPressedDetector._

  startWith(Uninitialized, UninitializedStateData)

  when(Uninitialized) {
    case Event(Init(runnableToExecuteOnKeyPress, checkIfKeyWasPressedTimeIntervalInMs), UninitializedStateData) ⇒ {
      setStateTimeout(WaitingForAKey, Some(checkIfKeyWasPressedTimeIntervalInMs millisecond))
      goto(WaitingForAKey) using new WaitingForAKeyStateData(runnableToExecuteOnKeyPress)
    }
  }

  when(WaitingForAKey) {
    case Event(StateTimeout, stateData: WaitingForAKeyStateData) ⇒ {
      if (wasKeyPressed()) {
        log.debug("Detected key press")
        try {
          stateData.runnableToExecuteOnKeyPress.run()
        } catch {
          case e: Exception ⇒
        }

        stop(FSM.Normal)
      } else {
        stay using stateData
      }
    }
  }

  onTransition {
    case fromState -> toState ⇒ log.debug(s"Change state from $fromState to $toState")
  }

  whenUnhandled {
    case Event(unknownMessage, stateData) ⇒ {
      log.warning(s"Received unknown message '$unknownMessage' (state data $stateData)")
      stay using stateData
    }
  }

  onTermination {
    case StopEvent(stopType, state, stateData) ⇒ {
      stopType match {
        case FSM.Normal         ⇒ log.info(s"Stopping (normal), state $state, data $stateData")
        case FSM.Shutdown       ⇒ log.info(s"Stopping (shutdown), state $state, data $stateData")
        case FSM.Failure(cause) ⇒ log.warning(s"Stopping (failure = $cause), state $state, data $stateData")
      }
    }
  }

  initialize

  protected def wasKeyPressed(): Boolean = {
    try {
      System.in.available > 0
    } catch {
      case e: Exception ⇒ false
    }
  }
}