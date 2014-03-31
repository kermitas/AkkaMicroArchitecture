package as.ama.addon.lifecycle

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor._
import as.akka.broadcaster._
import as.jvm._
import as.ama.startup.InitializationResult
import com.typesafe.config.Config

object LifecycleListener {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  case class ShutdownSystem(reason: Either[Exception, String]) extends IncomingMessage

  /**
   * Used when ShutdownSystem message will be placed on Akka's event bus (to be processed just after all log messages etc.)
   */
  case class WrappedShutdown(shutdown: ShutdownSystem) extends IncomingMessage
}

/**
 * Will shutdown system on LifecycleListener.ShutdownSystem message published on broadcaster.
 *
 * This actor is ready to be automatically initialized during ama startup. Should be defined on ama.initializeOnStartup.actors list
 * in application.conf, by default is defined in reference.conf (in ama-core project).
 *
 * @param commandLineArguments entered as arguments to program or defined in application.conf configuration file
 * @param config configuration defined in application.conf configuration file (for usage sample please see ama-sample project)
 * @param broadcaster main, pub-sub communication bus
 */
class LifecycleListener(commandLineArguments: Array[String], config: Config, broadcaster: ActorRef, runtimeProperties: Map[String, Any]) extends Actor with ActorLogging {

  import LifecycleListener._

  protected var lifecycleListenerConfig: LifecycleListenerConfig = _

  override def preStart() {
    try {
      lifecycleListenerConfig = LifecycleListenerConfig(config)
      broadcaster ! new Broadcaster.Register(self, new LifecycleListenerClassifier)

      broadcaster ! new InitializationResult(Right(None))
    } catch {
      case e: Exception => broadcaster ! new InitializationResult(Left(new Exception("Problem while installing lifecycle listener.", e)))
    }
  }

  override def postRestart(throwable: Throwable) = preStart()

  override def receive = {

    case shutdownSystem: ShutdownSystem => {
      if (lifecycleListenerConfig.onShutdownWaitForEventBusMessagesToBeProcessedInMs <= 0) {
        log.info("Performing immediate shutdown")
        performShutdown(shutdownSystem.reason)
        context.stop(self)
      } else {
        log.info("Wait for event bus messages to be processed and then perform shutdown")
        context.system.eventStream.subscribe(self, classOf[WrappedShutdown])
        context.system.scheduler.scheduleOnce(lifecycleListenerConfig.onShutdownWaitForEventBusMessagesToBeProcessedInMs milliseconds)(context.system.eventStream.publish(new WrappedShutdown(shutdownSystem)))(context.dispatcher)
      }
    }

    case WrappedShutdown(shutdownSystem) => {
      log.info("Delayed shutdown will be performed right now")
      performShutdown(shutdownSystem.reason)
      context.stop(self)
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }

  protected def performShutdown(reason: Either[Exception, String]) {
    val systemExitCode = reason match {
      case Right(description) => {
        log.info(s"Shutting down system with reason '$description'")
        0
      }
      case Left(exception) => {
        log.error(exception, s"Shutting down system with '$exception'.")
        -1
      }
    }

    log.info(s"Starting automatic JVM shutdown in ${lifecycleListenerConfig.automaticallyShutdownJVMAfterTimeInMs}ms...")
    new JVMSystemExitDaemon(lifecycleListenerConfig.automaticallyShutdownJVMAfterTimeInMs, systemExitCode).start

    log.info(s"Starting automatic JVM kill in ${lifecycleListenerConfig.automaticallyKillJVMAfterShutdownTryTimeInMs + lifecycleListenerConfig.automaticallyShutdownJVMAfterTimeInMs}ms...")
    new JVMRuntimeHaltDaemon(lifecycleListenerConfig.automaticallyKillJVMAfterShutdownTryTimeInMs + lifecycleListenerConfig.automaticallyShutdownJVMAfterTimeInMs, systemExitCode).start

    log.info("Performing actor system shutdown (if all will go well this should quickly finish JVM)...")
    context.system.shutdown()
  }
}
