package as.io

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{ Actor, FSM }

object InputStreamListener {
  sealed trait State extends Serializable
  case object Uninitialized extends State
  case object WaitingForAKey extends State

  sealed trait StateData extends Serializable
  case object UninitializedStateData extends StateData
  case class WaitingForAKeyStateData(inputStreamListenerCallback: InputStreamListenerCallback) extends StateData

  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message

  /**
   * Send this message to actor to init.
   *
   * @param inputStreamListenerCallback this is callback (will be executed on key press)
   * @param checkIfKeyWasPressedTimeIntervalInMs time interval between checks if key was pressed
   */
  case class Init(inputStreamListenerCallback: InputStreamListenerCallback, checkIfKeyWasPressedTimeIntervalInMs: Int) extends IncomingMessage
}

/**
 * Will execute inputStreamListenerCallback when something will be available on input stream (System.in).
 *
 * Send Init message to initialize and start listening for a keys.
 */
class InputStreamListener extends Actor with FSM[InputStreamListener.State, InputStreamListener.StateData] {

  import InputStreamListener._

  startWith(Uninitialized, UninitializedStateData)

  when(Uninitialized) {
    case Event(Init(inputStreamListenerCallback, checkIfKeyWasPressedTimeIntervalInMs), UninitializedStateData) => {
      setStateTimeout(WaitingForAKey, Some(checkIfKeyWasPressedTimeIntervalInMs millisecond))
      goto(WaitingForAKey) using new WaitingForAKeyStateData(inputStreamListenerCallback)
    }
  }

  when(WaitingForAKey) {
    case Event(StateTimeout, stateData: WaitingForAKeyStateData) => {

      nonBlockingReadLine() match {

        case Some(line) => {
          log.debug(s"Read input stream (${line.length} characters):$line")

          val continue = try {
            stateData.inputStreamListenerCallback.inputStreamNotification(line)
          } catch {
            case e: Exception => false
          }

          if (continue)
            stay using stateData
          else
            stop(FSM.Normal)
        }

        case None => stay using stateData
      }
    }
  }

  onTransition {
    case fromState -> toState => log.debug(s"Change state from $fromState to $toState")
  }

  whenUnhandled {
    case Event(unknownMessage, stateData) => {
      log.warning(s"Received unknown message '$unknownMessage' (state data $stateData)")
      stay using stateData
    }
  }

  onTermination {
    case StopEvent(stopType, state, stateData) => {
      stopType match {
        case FSM.Normal         => log.info(s"Stopping (normal), state $state, data $stateData")
        case FSM.Shutdown       => log.info(s"Stopping (shutdown), state $state, data $stateData")
        case FSM.Failure(cause) => log.warning(s"Stopping (failure = $cause), state $state, data $stateData")
      }
    }
  }

  initialize

  /**
   * Will try to read input stream safely.
   *
   * @return text that was provided (mostly to console);
   *         never null, if just [enter] was hit then empty string will be returned; if there were no text then None will be returned
   */
  protected def nonBlockingReadLine(): Option[String] = {
    try {
      val line = if (System.in.available > 0) System.console.readLine else null

      if (line != null) {
        Some(line)
      } else {
        None
      }
    } catch {
      case e: Exception => None
    }
  }
}