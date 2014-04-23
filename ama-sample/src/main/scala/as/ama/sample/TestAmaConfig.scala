package as.ama.sample

import com.typesafe.config.Config
import akka.actor.ActorRef
import as.ama.startup.AmaConfig

class TestAmaConfig(commandLineArguments: Array[String], config: Config, broadcaster: ActorRef, initializationResultListener: ActorRef, val testString: String) extends AmaConfig(commandLineArguments, config, broadcaster, initializationResultListener)