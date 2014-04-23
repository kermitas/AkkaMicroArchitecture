package as.ama.startup

import akka.actor.{ FSM, ActorRef }

/**
 * Useful FSM actor in Ama world.
 *
 * How it works? When you will leave first state (onTransmission) it will send successful InitializationResult to initializationResultListener.
 *
 * But when something will go wrong in first state then you should stop with FSM.Failure, to do that use successOrStop().
 *
 * For termination use amaTerminate() and it will detect if you fial in first state, if yes it will send failure InitializationResult to initializationResultListener.
 *
 * First please define your initial state and initializationResultListener:
 * amaStartWith(Initializing, amaSessionConfig.initializationResultListener)
 *
 * Then on first state use successOrStop():
 * when(Initializing) {
 *   case Event(true, InitializingStateData) => successOrStop { ... }
 * }
 *
 * Then define onTermination like:
 * onTermination { amaTerminate }
 */
trait AmaStartupFSM[S, D] extends FSM[S, D] {

  protected var initialState: S = _
  protected var initializationResultListener: ActorRef = _

  /**
   * We are leaving initial state so initialization went ok, sending successful InitializationResult to initializationResultListener.
   */
  onTransition {
    case fromState -> toState => if (fromState == initialState) initializationResultListener ! new InitializationResult(Right(None))
  }

  /**
   * Remember to call this method before fsm initialize, for example:
   *
   * amaStartWith(Initializing, amaSessionConfig.initializationResultListener)
   */
  def amaStartWith(initialState: S, initializationResultListener: ActorRef) {
    this.initialState = initialState
    this.initializationResultListener = initializationResultListener
  }

  /**
   * Use this method to surround initialization, for example:
   *
   * when(Initializing) {
   *   case Event(true, InitializingStateData) => successOrStop { ... }
   * }
   */
  def successOrStopWithFailure(f: => State): State = {
    try {
      f
    } catch {
      case e: Exception => stop(FSM.Failure(e))
    }
  }

  /**
   * When terminating in first state with failure then it will send unsuccessful InitializationResult to initializationResultListener.
   *
   * Usage example:
   * onTermination { amaTerminate }
   */
  def amaTerminate: PartialFunction[StopEvent, Unit] = {
    case StopEvent(FSM.Normal, currentState, stateData)   => log.debug(s"Stopping (normal), state $currentState, data $stateData")

    case StopEvent(FSM.Shutdown, currentState, stateData) => log.debug(s"Stopping (shutdown), state $currentState, data $stateData")

    case StopEvent(FSM.Failure(cause), currentState, stateData) => {
      log.warning(s"Stopping (failure, cause $cause), state $currentState, data $stateData")

      if (currentState == initialState) {
        val e = new Exception(s"Problem while installing ${getClass.getName} actor.")
        if (cause.isInstanceOf[Exception]) e.initCause(cause.asInstanceOf[Exception])
        initializationResultListener ! new InitializationResult(Left(e))
      }
    }
  }
}