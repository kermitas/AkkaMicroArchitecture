package as.ama.startup

import com.typesafe.config.{ ConfigFactory, Config }

/**
 * Configuration read from JSON (HOCON) file.
 */
object InitializeOnStartupActorConfig {

  final val clazzNameConfigKey = "class"
  final val configConfigKey = "config"
  final val initializationOrderConfigKey = "initializationOrder"
  final val initializationTimeoutInMsConfigKey = "initializationTimeoutInMs"
  final val defaultInitializationOrder = 1000

  def apply(config: Config, defaultSingleActorInitializationTimeoutInMs: Int): InitializeOnStartupActorConfig = {
    val clazzName = config.getString(clazzNameConfigKey)
    val initializationOrder = if (config.hasPath(initializationOrderConfigKey)) config.getInt(initializationOrderConfigKey) else defaultInitializationOrder
    val cfg = if (config.hasPath(configConfigKey)) config.getConfig(configConfigKey) else ConfigFactory.empty

    val initializationTimeoutInMs = if (config.hasPath(initializationTimeoutInMsConfigKey)) {
      val value = config.getInt(initializationTimeoutInMsConfigKey)

      if (value <= 0)
        defaultSingleActorInitializationTimeoutInMs
      else
        value

    } else {
      defaultSingleActorInitializationTimeoutInMs
    }

    new InitializeOnStartupActorConfig(clazzName, initializationOrder, initializationTimeoutInMs, cfg)
  }
}

case class InitializeOnStartupActorConfig(clazzName: String, initializationOrder: Int, initializationTimeoutInMs: Int, config: Config) extends Serializable
