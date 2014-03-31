package as.ama.startup

import scala.collection.JavaConverters._
import com.typesafe.config.Config

/**
 * Configuration read from JSON (HOCON) file.
 */
object InitializeOnStartupConfig {

  final val actorInitializationTimeoutInMsConfigKey = "actorInitializationTimeoutInMs"
  final val generalInitializationTimeoutInMsConfigKey = "generalInitializationTimeoutInMs"
  final val runtimePropertiesBuilderClassNameConfigKey = "runtimePropertiesBuilderClassName"
  final val actorsConfigKey = "actors"

  def apply(config: Config): InitializeOnStartupConfig = {
    val actorInitializationTimeoutInMs = config.getInt(actorInitializationTimeoutInMsConfigKey)
    val initializeOnStartupActorConfigs = config.getConfigList(actorsConfigKey).asInstanceOf[java.util.List[Config]].asScala.map(InitializeOnStartupActorConfig(_))
    val generalInitializationTimeoutInMs = config.getInt(generalInitializationTimeoutInMsConfigKey)
    val runtimePropertiesBuilderClassName = if (config.hasPath(runtimePropertiesBuilderClassNameConfigKey)) config.getString(runtimePropertiesBuilderClassNameConfigKey) else classOf[EmptyRuntimePropertiesBuilder].getName

    new InitializeOnStartupConfig(actorInitializationTimeoutInMs, generalInitializationTimeoutInMs, initializeOnStartupActorConfigs.sortWith(_.initializationOrder < _.initializationOrder), runtimePropertiesBuilderClassName)
  }
}

case class InitializeOnStartupConfig(actorInitializationTimeoutInMs: Int, generalInitializationTimeoutInMs: Int, initializeOnStartupActorConfigs: Seq[InitializeOnStartupActorConfig], runtimePropertiesBuilderClassName: String) extends Serializable