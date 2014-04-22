package as.ama.startup

import scala.collection.JavaConverters._
import com.typesafe.config.Config

/**
 * Configuration read from JSON (HOCON) file.
 */
object InitializeOnStartupConfig {

  final val defaultActorInitializationTimeoutInMsConfigKey = "defaultSingleActorInitializationTimeoutInMs"
  final val amaConfigBuilderClassNameConfigKey = "amaConfigBuilderClassName"
  final val actorsConfigKey = "actors"

  def apply(config: Config): InitializeOnStartupConfig = {
    val defaultSingleActorInitializationTimeoutInMs = config.getInt(defaultActorInitializationTimeoutInMsConfigKey)
    val initializeOnStartupActorConfigs = config.getConfigList(actorsConfigKey).asInstanceOf[java.util.List[Config]].asScala.map(InitializeOnStartupActorConfig(_, defaultSingleActorInitializationTimeoutInMs))
    val amaConfigBuilderClassName = config.getString(amaConfigBuilderClassNameConfigKey)

    new InitializeOnStartupConfig(defaultSingleActorInitializationTimeoutInMs, initializeOnStartupActorConfigs.sortWith(_.initializationOrder < _.initializationOrder), amaConfigBuilderClassName)
  }
}

case class InitializeOnStartupConfig(defaultSingleActorInitializationTimeoutInMs: Int, initializeOnStartupActorConfigs: Seq[InitializeOnStartupActorConfig], amaConfigBuilderClassName: String) extends Serializable