package as.ama.startup

import scala.collection.JavaConverters._
import com.typesafe.config.Config

object InitializeOnStartupConfig {

  final val actorInitializationTimeoutInMsConfigKey = "actorInitializationTimeoutInMs"
  final val generalInitializationTimeoutInMsConfigKey = "generalInitializationTimeoutInMs"
  final val actorsConfigKey = "actors"

  def apply(config: Config): InitializeOnStartupConfig = {
    val actorInitializationTimeoutInMs = config.getInt(actorInitializationTimeoutInMsConfigKey)
    val initializeOnStartupActorConfigs = config.getConfigList(actorsConfigKey).asInstanceOf[java.util.List[Config]].asScala.map(InitializeOnStartupActorConfig(_))
    val generalInitializationTimeoutInMs = config.getInt(generalInitializationTimeoutInMsConfigKey)

    new InitializeOnStartupConfig(actorInitializationTimeoutInMs, generalInitializationTimeoutInMs, initializeOnStartupActorConfigs.sortWith(_.initializationOrder < _.initializationOrder))
  }
}

case class InitializeOnStartupConfig(actorInitializationTimeoutInMs: Int, generalInitializationTimeoutInMs: Int, initializeOnStartupActorConfigs: Seq[InitializeOnStartupActorConfig]) extends Serializable