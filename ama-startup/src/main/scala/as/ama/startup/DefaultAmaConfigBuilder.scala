package as.ama.startup

import akka.actor.ActorRef
import com.typesafe.config.Config

class DefaultAmaConfigBuilder extends AmaConfigBuilder {
  override def createAmaConfig(clazzName: String, commandLineArguments: Array[String], config: Config,
                               broadcaster: ActorRef, initializationResultListener: ActorRef, amaRootActor: ActorRef): AmaConfig = {
    new AmaConfig(commandLineArguments, config, broadcaster, initializationResultListener, amaRootActor)
  }

}
