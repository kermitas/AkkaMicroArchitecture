package as.ama.startup

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{ Actor, FSM, ActorRef, Cancellable }
import as.akka.util.CreateActorExecuteInActorsContext

object StartupInitializerWorker extends Serializable {

  sealed trait State extends Serializable
  case object WaitingForJob extends State
  case object InitializingActors extends State
  case object SingleActorInitialization extends State

  sealed trait StateData extends Serializable
  case object WaitingForJobStateData extends StateData
  case class InitializingActorsStateData(ic: StartupInitializer.InitialConfiguration, generalInitializationTimeout: Cancellable, generalInitializationTimeoutInMs: Int, amaRootActor: ActorRef) extends StateData
  case class SingleActorInitializationStateData(ic: StartupInitializer.InitialConfiguration, generalInitializationTimeout: Cancellable, initializingActorIndex: Int, actorInitializationTimeout: Cancellable, generalInitializationTimeoutInMs: Int, amaRootActor: ActorRef) extends StateData

  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  case class InitialConfiguration(ic: StartupInitializer.InitialConfiguration, amaRootActor: ActorRef) extends IncomingMessage
  case object GeneralInitializationTimeout extends IncomingMessage
  case object SingleActorInitializationTimeout extends IncomingMessage

  case class InitializeActor(initializingActorIndex: Int) extends IncomingMessage

  //def classifier = new StartupInitializerWorkerClassifier
}

/**
 * Will instantiate actors defined on ama.initializeOnStartup.actors list (in reference.conf or application.conf).
 *
 * General initialization timeout is a sum of all actors initialization timeout.
 *
 * Please see documentation folder for diagrams with detailed message flows.
 *
 * Please see ama-sample for basic usage.
 */
class StartupInitializerWorker extends Actor with FSM[StartupInitializerWorker.State, StartupInitializerWorker.StateData] {

  import StartupInitializerWorker._

  startWith(WaitingForJob, WaitingForJobStateData)

  when(WaitingForJob) {
    case Event(ic: InitialConfiguration, WaitingForJobStateData) => newJobArrived(ic.ic, ic.amaRootActor)
  }

  when(InitializingActors) {
    case Event(GeneralInitializationTimeout, sd: SingleActorInitializationStateData)     => generalInitializationTimeout(sd.generalInitializationTimeoutInMs, sd.ic, None)
    case Event(InitializeActor(initializingActorIndex), sd: InitializingActorsStateData) => initializeActor(initializingActorIndex, sd)
  }

  when(SingleActorInitialization) {
    case Event(GeneralInitializationTimeout, sd: SingleActorInitializationStateData)                  => generalInitializationTimeout(sd.generalInitializationTimeoutInMs, sd.ic, Some(sd.actorInitializationTimeout))
    case Event(SingleActorInitializationTimeout, sd: SingleActorInitializationStateData)              => singleActorInitializationTimeout(sd.initializingActorIndex, sd.ic)
    case Event(ir: InitializationResult, sd: SingleActorInitializationStateData) if ir.result.isLeft  => singleActorInitializationFail(ir.result.left.get, sd)
    case Event(ir: InitializationResult, sd: SingleActorInitializationStateData) if ir.result.isRight => singleActorInitializationSuccess(sd)
  }

  onTransition {
    case fromState -> toState => log.debug(s"State change from $fromState to $toState")
  }

  whenUnhandled {
    case Event(unknownMessage, stateData) => {
      log.warning(s"Received unknown message '$unknownMessage' in state $stateName (state data $stateData)")
      stay using stateData
    }
  }

  onTermination {
    case se: StopEvent => terminate(se)
  }

  initialize

  protected def newJobArrived(ic: StartupInitializer.InitialConfiguration, amaRootActor: ActorRef) = {
    if (ic.initializeOnStartupConfig.initializeOnStartupActorConfigs.isEmpty) {
      log.debug("Noting to do")
      stay using WaitingForJobStateData
    } else {
      log.debug("Initialization order:")
      for (initializeOnStartupActorConfig ← ic.initializeOnStartupConfig.initializeOnStartupActorConfigs) log.debug(s"${initializeOnStartupActorConfig.clazzName}, initializationOrder ${initializeOnStartupActorConfig.initializationOrder}")

      val generalInitializationTimeoutInMs = ic.initializeOnStartupConfig.initializeOnStartupActorConfigs.foldLeft(0)(_ + _.initializationTimeoutInMs)
      val generalInitializationTimeout = context.system.scheduler.scheduleOnce(generalInitializationTimeoutInMs millis, self, GeneralInitializationTimeout)(context.dispatcher)

      self ! new InitializeActor(0)

      goto(InitializingActors) using new InitializingActorsStateData(ic, generalInitializationTimeout, generalInitializationTimeoutInMs, amaRootActor)
    }
  }

  protected def initializeActor(initializingActorIndex: Int, sd: InitializingActorsStateData): State = {
    initializeActor(sd.ic.initializeOnStartupConfig.initializeOnStartupActorConfigs(initializingActorIndex), initializingActorIndex, sd)
  }

  protected def initializeActor(initializeOnStartupActorConfig: InitializeOnStartupActorConfig, initializingActorIndex: Int, sd: InitializingActorsStateData): State = {
    try {
      val amaConfig = sd.ic.amaConfigBuilder.createAmaConfig(initializeOnStartupActorConfig.clazzName, sd.ic.commandLineArguments, initializeOnStartupActorConfig.config, sd.ic.broadcaster, self)
      val props = PropsCreator.createProps(initializeOnStartupActorConfig.clazzName, amaConfig)

      sd.amaRootActor ! new CreateActorExecuteInActorsContext(props, initializeOnStartupActorConfig.clazzName)

      val actorInitializationTimeout = context.system.scheduler.scheduleOnce(initializeOnStartupActorConfig.initializationTimeoutInMs milliseconds, self, SingleActorInitializationTimeout)(context.dispatcher)

      goto(SingleActorInitialization) using new SingleActorInitializationStateData(sd.ic, sd.generalInitializationTimeout, initializingActorIndex, actorInitializationTimeout, sd.generalInitializationTimeoutInMs, sd.amaRootActor)
    } catch {
      case e: Exception => {
        log.error(e, s"Could not create actor ${initializeOnStartupActorConfig.clazzName}.")
        sd.ic.broadcaster ! new StartupInitializer.ProblemWhileInitializeActors(e, sd.ic)
        stop(FSM.Failure(e))
      }
    }
  }

  protected def singleActorInitializationFail(initializationException: Exception, sd: SingleActorInitializationStateData) = {
    sd.actorInitializationTimeout.cancel()
    sd.generalInitializationTimeout.cancel()

    val initializeOnStartupActorConfig = sd.ic.initializeOnStartupConfig.initializeOnStartupActorConfigs(sd.initializingActorIndex)
    val clazzName = initializeOnStartupActorConfig.clazzName

    val e = new Exception(s"Actor $clazzName initialization fail.", initializationException)
    sd.ic.broadcaster ! new StartupInitializer.ProblemWhileInitializeActors(e, sd.ic)

    stop(FSM.Failure(e))
  }

  protected def singleActorInitializationTimeout(initializingActorIndex: Int, ic: StartupInitializer.InitialConfiguration) = {
    val initializeOnStartupActorConfig = ic.initializeOnStartupConfig.initializeOnStartupActorConfigs(initializingActorIndex)
    val clazzName = initializeOnStartupActorConfig.clazzName
    val initializationTimeoutInMs = initializeOnStartupActorConfig.initializationTimeoutInMs

    val ite = new InitializationTimeoutException(s"Timeout ($initializationTimeoutInMs ms) while initializing actor $clazzName.")
    ic.broadcaster ! new StartupInitializer.ProblemWhileInitializeActors(ite, ic)
    stop(FSM.Failure(ite))
  }

  protected def singleActorInitializationSuccess(sd: SingleActorInitializationStateData) = {
    sd.actorInitializationTimeout.cancel()

    val nextInitializingActorIndex = sd.initializingActorIndex + 1

    if (nextInitializingActorIndex < sd.ic.initializeOnStartupConfig.initializeOnStartupActorConfigs.size) {
      self ! new InitializeActor(nextInitializingActorIndex)
      goto(InitializingActors) using new InitializingActorsStateData(sd.ic, sd.generalInitializationTimeout, sd.generalInitializationTimeoutInMs, sd.amaRootActor)
    } else {
      sd.generalInitializationTimeout.cancel()
      sd.ic.broadcaster ! new StartupInitializer.AllActorsWereInstantiatedCorrectly(sd.ic)
      stop(FSM.Normal)
    }
  }

  protected def generalInitializationTimeout(generalInitializationTimeoutInMs: Int, ic: StartupInitializer.InitialConfiguration, actorInitializationTimeout: Option[Cancellable]) = {
    actorInitializationTimeout map { _.cancel() }
    val ite = new InitializationTimeoutException(s"General timeout ($generalInitializationTimeoutInMs ms) while initializing actors on startup.")
    ic.broadcaster ! new StartupInitializer.ProblemWhileInitializeActors(ite, ic)
    stop(FSM.Failure(ite))
  }

  protected def terminate(se: StopEvent) {
    se match {
      case StopEvent(FSM.Normal, se.currentState, stateData)         => log.debug(s"Stopping (normal), state ${se.currentState}, data $stateData")
      case StopEvent(FSM.Shutdown, se.currentState, stateData)       => log.debug(s"Stopping (shutdown), state ${se.currentState}, data $stateData")
      case StopEvent(FSM.Failure(cause), se.currentState, stateData) => log.warning(s"Stopping (failure, cause $cause), state ${se.currentState}, data $stateData")
    }
  }
}