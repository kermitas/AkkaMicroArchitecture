package as.ama.startup

import com.typesafe.config.Config
import akka.actor.ActorRef

/**
 * If not defined in configuration file then instance of this class will be used to produce empty runtime properties.
 */
class EmptyRuntimePropertiesBuilder extends RuntimePropertiesBuilder {
  override def createRuntimeProperties(clazzName: String, commandLineArguments: Array[String], config: Config, broadcaster: ActorRef): Map[String, Any] = Map.empty
}
