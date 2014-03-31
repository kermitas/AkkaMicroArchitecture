package as.ama.startup

import com.typesafe.config.Config
import akka.actor.ActorRef

/**
 * Thanks to this class you can pass runtime properties to each created actor.
 */
trait RuntimePropertiesBuilder extends Serializable {
  def createRuntimeProperties(clazzName: String, commandLineArguments: Array[String], config: Config, broadcaster: ActorRef): Map[String, Any]
}
