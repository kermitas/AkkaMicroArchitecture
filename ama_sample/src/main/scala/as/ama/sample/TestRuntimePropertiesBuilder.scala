package as.ama.sample

import com.typesafe.config.Config
import akka.actor.ActorRef
import as.ama.startup.RuntimePropertiesBuilder

/**
 * Test runtime properties builder
 */
class TestRuntimePropertiesBuilder extends RuntimePropertiesBuilder {
  override def createRuntimeProperties(clazzName: String, commandLineArguments: Array[String], config: Config, broadcaster: ActorRef): Map[String, Any] = {
    Map("testKey" -> "testValue")
  }
}
