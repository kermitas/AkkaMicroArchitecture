package as.ama.addon.lifecycle

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor._
import as.akka.broadcaster._
import as.jvm._
import as.ama.startup.InitializationResult
import as.ama.startup.AmaConfig

object LifecycleManager {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message

  /**
   * Used when ShutdownSystem message will be placed on Akka's event bus (to be processed just after all log messages etc.)
   */
  case class WrappedShutdown(shutdown: ShutdownSystem) extends IncomingMessage
}

/**
 * Will shutdown system on ShutdownSystem message published on broadcaster.
 *
 * This actor is ready to be automatically initialized during ama startup. Should be defined on ama.initializeOnStartup.actors list
 * in application.conf, by default is defined in reference.conf (in ama-core project).
 */
class LifecycleManager(amaConfig: AmaConfig) extends Actor with ActorLogging {

  import LifecycleManager._

  protected var lifecycleListenerConfig: LifecycleManagerConfig = _

  /**
   * Will be executed when actor is created and also after actor restart (if postRestart() is not override).
   */
  override def preStart() {
    try {
      lifecycleListenerConfig = LifecycleManagerConfig(amaConfig.config)
      amaConfig.broadcaster ! new Broadcaster.Register(self, new LifecycleManagerClassifier)

      amaConfig.broadcaster ! new InitializationResult(Right(None))
    } catch {
      case e: Exception => amaConfig.broadcaster ! new InitializationResult(Left(new Exception("Problem while installing lifecycle listener.", e)))
    }
  }

  override def receive = {

    case shutdownSystem: ShutdownSystem => {
      if (lifecycleListenerConfig.onShutdownWaitForEventBusMessagesToBeProcessedInMs <= 0) {
        log.info("Performing immediate shutdown")
        performShutdown(shutdownSystem.reason)
        context.stop(self)
      } else {
        log.info("Wait for event bus messages to be processed and then perform shutdown")
        context.system.eventStream.subscribe(self, classOf[WrappedShutdown])
        val wrappedShutdownSystemMessage = new WrappedShutdown(shutdownSystem)
        context.system.scheduler.scheduleOnce(lifecycleListenerConfig.onShutdownWaitForEventBusMessagesToBeProcessedInMs milliseconds)(context.system.eventStream.publish(wrappedShutdownSystemMessage))(context.dispatcher)
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
