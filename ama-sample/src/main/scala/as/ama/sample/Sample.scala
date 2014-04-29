package as.ama.sample

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{ Actor, ActorLogging }
import as.akka.broadcaster.Broadcaster
import as.ama.addon.inputstream.InputStreamText
import as.ama.addon.lifecycle.ShutdownSystem

object Sample {
  sealed trait Message extends Serializable
  sealed trait IncomingMessage extends Message
  case object TestMessage extends IncomingMessage
}

/**
 * If you want that your actor should be initialized during AkkaMicroArchitecture startup
 * please define it under ama.initializeOnStartup.actors (please see application.conf):
 *
 * {{{
 *       {
 *       class = "as.ama.sample.Sample"
 *
 *       config = {
 *           test = "Alice"
 *       }
 *     }
 * }}}
 *
 * Your actor will be initialized by AkkaMicroArchitecture with as.ama.startup.AmaConfig as an argument.
 * This argument is crated by AmaConfigBuilder; AmaConfigBuilder can be provided by you.
 */
class Sample(testAmaConfig: TestAmaConfig) extends Actor with ActorLogging {

  import Sample._

  protected var count = 0

  /**
   * Will be executed when actor is created and also after actor restart (if postRestart() is not override).
   */
  override def preStart() {
    try {
      // notifying broadcaster to register us with given classifier
      testAmaConfig.broadcaster ! new Broadcaster.Register(self, new SampleClassifier)

      // scheduling TestMessage that will be send to broadcaster every 1 second repeatedly
      context.system.scheduler.schedule(1 seconds, 1 seconds, testAmaConfig.broadcaster, TestMessage)(context.dispatcher)

      // for a test purposes we are publishing string on broadcaster
      testAmaConfig.broadcaster ! s"==================> Hello from sample actor (via broadcaster), command line arguments: ${testAmaConfig.commandLineArguments.mkString(",")}. <=================="

      // for a test purposes we are publishing 'test' key from configuration (please see application.conf)
      testAmaConfig.broadcaster ! s"==================> Config: test = ${testAmaConfig.config.getString("test")}. <=================="

      // for a test purposes we are publishing keys from runtimeProperties
      testAmaConfig.broadcaster ! s"==================> Config: testString from amaConfig = ${testAmaConfig.testString}. <=================="

      // remember always to send back how your initialization goes
      testAmaConfig.sendInitializationResult()
    } catch {
      case e: Exception => testAmaConfig.sendInitializationResult(new Exception("Problem while installing sample actor.", e))
    }
  }

  override def receive = {

    // any string published on broadcaster is forwarded to us
    case s: String => log.info(s"Received string '$s' from broadcaster.")

    // received text from console
    case InputStreamText(inputText) => {
      log.info(s"Input text (${inputText.length} characters):$inputText")

      if (inputText.isEmpty) {
        log.info("Empty input string means that we will finish!")
        testAmaConfig.broadcaster ! new ShutdownSystem(Right("[enter] was pressed in console"))
        context.stop(self)
      }
    }

    // we are also interested in TestMessage (please see SampleClassifier)
    case TestMessage => {
      count += 1
      log.info(s"Received test message for the $count time.")
    }

    case message => log.warning(s"Unhandled $message send by ${sender()}")
  }
}