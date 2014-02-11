package as.ama.startup

import com.typesafe.config.{ ConfigFactory, Config }

object InitializeOnStartupActorConfig {

  final val clazzNameConfigKey = "class"
  final val configConfigKey = "config"
  final val initializationOrderConfigKey = "initializationOrder"
  final val defaultInitializationOrder = 1000

  def apply(config: Config): InitializeOnStartupActorConfig = {
    val clazzName = config.getString(clazzNameConfigKey)
    val initializationOrder = if (config.hasPath(initializationOrderConfigKey)) config.getInt(initializationOrderConfigKey) else defaultInitializationOrder
    val cfg = if (config.hasPath(configConfigKey)) config.getConfig(configConfigKey) else ConfigFactory.empty

    new InitializeOnStartupActorConfig(clazzName, initializationOrder, cfg)
  }
}

case class InitializeOnStartupActorConfig(clazzName: String, initializationOrder: Int, config: Config) extends Serializable
