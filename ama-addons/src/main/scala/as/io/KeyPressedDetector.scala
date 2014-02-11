package as.io

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor._

object KeyPressedDetector {
  sealed trait State extends Serializable
  case object Uninitialized extends State
  case object WaitingForAKey extends State

  sealed trait StateData extends Serializable
  case object UninitializedStateData extends StateData
  case class WaitingForAKeyStateData(executeOnKeyPress: ExecuteOnKeyPress) extends StateData

  sealed trait Messages extends Serializable
  sealed trait IncomingMessages extends Messages

  /**
   * Send this message to actor to init.
   *
   * @param executeOnKeyPress this is callback (will be executed on key press)
   * @param checkIfKeyWasPressedTimeIntervalInMs time interval between checks if key was pressed
   */
  case class Init(executeOnKeyPress: ExecuteOnKeyPress, checkIfKeyWasPressedTimeIntervalInMs: Int) extends IncomingMessages
}

/**
 * Will execute executeOnKeyPress once key was pressed.
 *
 * Send Init message to initialize and start listening for a keys.
 */
class KeyPressedDetector extends Actor with FSM[KeyPressedDetector.State, KeyPressedDetector.StateData] {

  import KeyPressedDetector._

  startWith(Uninitialized, UninitializedStateData)

  when(Uninitialized) {
    case Event(Init(executeOnKeyPress, checkIfKeyWasPressedTimeIntervalInMs), UninitializedStateData) ⇒ {
      setStateTimeout(WaitingForAKey, Some(checkIfKeyWasPressedTimeIntervalInMs millisecond))
      goto(WaitingForAKey) using new WaitingForAKeyStateData(executeOnKeyPress)
    }
  }

  when(WaitingForAKey) {
    case Event(StateTimeout, stateData: WaitingForAKeyStateData) ⇒ {

      readLine() match {

        case Some(line) ⇒ {
          log.debug(s"Detected key press: $line")

          val continue = try {
            stateData.executeOnKeyPress.keyWasPressedNotification(line)
          } catch {
            case e: Exception ⇒ false
          }

          if (continue)
            stay using stateData
          else
            stop(FSM.Normal)
        }

        case None ⇒ stay using stateData
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

  protected def readLine(): Option[String] = {
    try {
      val line = Console.readLine()

      if (line != null && line.length > 0)
        Some(line)
      else
        None
    } catch {
      case e: Exception ⇒ None
    }
  }
}