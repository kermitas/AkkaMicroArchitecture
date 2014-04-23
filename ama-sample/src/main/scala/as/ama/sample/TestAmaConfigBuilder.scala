package as.ama.sample

import com.typesafe.config.Config
import akka.actor.ActorRef
import as.ama.startup.{ AmaConfig, AmaConfigBuilder }

/**
 * Test runtime properties builder
 */
class TestAmaConfigBuilder extends AmaConfigBuilder {
  override def createAmaConfig(clazzName: String, commandLineArguments: Array[String], config: Config, broadcaster: ActorRef, initializationResultListener: ActorRef): AmaConfig = {
    new TestAmaConfig(commandLineArguments, config, broadcaster, initializationResultListener, "ABC_123")
  }
}
