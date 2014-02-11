package as.ama.sample

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor._
import as.ama.startup.InitializationResult
import as.akka.broadcaster._
import com.typesafe.config.Config

object Sample {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  case object TestMessage extends IncomingMessage
}

/**
 * If you want that your actor should be initialized during AkkaMicroArchitecture startup
 * please define it under ama.initializeOnStartup.actors (please see application.conf):
 *
 *       {
 *       class = "as.ama.sample.Sample"
 *       initializationOrder = 1000
 *
 *       config = {
 *           test = "Alice"
 *       }
 *     }
 *
 * Your actor will be initialized by AkkaMicroArchitecture with arguments:
 * commandLineArguments: Array[String], config: Config, broadcaster: ActorRef
 *
 */
class Sample(commandLineArguments: Array[String], config: Config, broadcaster: ActorRef) extends Actor with ActorLogging {

  import Sample._

  protected var count = 0

  override def preStart() {
    try {
      // asking broadcaster to register us with given classifier
      broadcaster ! new Broadcaster.Register(self, new SampleClassifier)

      // scheduling TestMessage that will be send to broadcaster every 1 second repeatedly
      context.system.scheduler.schedule(1 seconds, 1 seconds, broadcaster, TestMessage)(context.dispatcher)

      // for a test purposes we are publishing string on broadcaster
      broadcaster ! s"==================> Hello from sample actor (via broadcaster), command line arguments: ${commandLineArguments.mkString(",")}. <=================="

      // for a test purposes we are publishing 'test' key from configuration (please see application.conf)
      broadcaster ! s"==================> Config: test = ${config.getString("test")}. <=================="

      // remember always to send back how your initialization goes
      broadcaster ! new InitializationResult(Right(None))
    } catch {
      case e: Exception ⇒ broadcaster ! new InitializationResult(Left(new Exception("Problem while installing sample actor.", e)))
    }
  }

  override def postRestart(throwable: Throwable) = preStart()

  override def receive = {

    // any string published on broadcaster is forwarded to us
    case s: String ⇒ log.info(s"Received string '$s' from broadcaster.")

    // we are also interested in TestMessage (please see SampleClassifier)
    case TestMessage ⇒ {
      count += 1
      log.info(s"Received test message for the $count time.")
    }

    case message ⇒ log.warning(s"Unhandled $message send by ${sender()}")
  }
}