package as.ama.addon.keypressdetector

import akka.actor._
import as.io._
import as.ama.startup.InitializationResult
import as.ama.addon.lifecycle._
import com.typesafe.config.Config

object InstallKeyPressDetector {
  final val checkIfKeyWasPressedTimeIntervalInMsConfigKey = "checkIfKeyWasPressedTimeIntervalInMs"
}

class InstallKeyPressDetector(commandLineArguments: Array[String], config: Config, broadcaster: ActorRef) extends Actor with ActorLogging {

  import InstallKeyPressDetector._

  override def preStart() {
    try {
      val keyPressDetector = context.system.actorOf(Props[KeyPressedDetector], classOf[KeyPressedDetector].getSimpleName)
      val keyPressedAction = new KeyPressedAction(broadcaster, new LifecycleListener.ShutdownSystem(Right("Key was pressed.")))
      val checkIfKeyWasPressedTimeIntervalInMs = config.getInt(checkIfKeyWasPressedTimeIntervalInMsConfigKey)

      keyPressDetector ! new KeyPressedDetector.Init(keyPressedAction, checkIfKeyWasPressedTimeIntervalInMs)

      broadcaster ! new InitializationResult(Right(None))
    } catch {
      case e: Exception ⇒ broadcaster ! new InitializationResult(Left(new Exception("Problem while installing key press detector.", e)))
    } finally {
      context.stop(self)
    }
  }

  override def postRestart(throwable: Throwable) = preStart()

  override def receive = {

    case message ⇒ log.warning(s"Unhandled $message send by ${sender()}")
  }
}