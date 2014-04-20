package as.ama.startup

import scala.collection.JavaConverters._
import com.typesafe.config.Config

/**
 * Configuration read from JSON (HOCON) file.
 */
object InitializeOnStartupConfig {

  final val actorInitializationTimeoutInMsConfigKey = "actorInitializationTimeoutInMs"
  final val generalInitializationTimeoutInMsConfigKey = "generalInitializationTimeoutInMs"
  final val amaConfigBuilderClassNameConfigKey = "amaConfigBuilderClassName"
  final val actorsConfigKey = "actors"

  def apply(config: Config): InitializeOnStartupConfig = {
    val actorInitializationTimeoutInMs = config.getInt(actorInitializationTimeoutInMsConfigKey)
    val initializeOnStartupActorConfigs = config.getConfigList(actorsConfigKey).asInstanceOf[java.util.List[Config]].asScala.map(InitializeOnStartupActorConfig(_))
    val generalInitializationTimeoutInMs = config.getInt(generalInitializationTimeoutInMsConfigKey)
    val amaConfigBuilderClassName = config.getString(amaConfigBuilderClassNameConfigKey)

    new InitializeOnStartupConfig(actorInitializationTimeoutInMs, generalInitializationTimeoutInMs, initializeOnStartupActorConfigs.sortWith(_.initializationOrder < _.initializationOrder), amaConfigBuilderClassName)
  }
}

case class InitializeOnStartupConfig(actorInitializationTimeoutInMs: Int, generalInitializationTimeoutInMs: Int, initializeOnStartupActorConfigs: Seq[InitializeOnStartupActorConfig], amaConfigBuilderClassName: String) extends Serializable