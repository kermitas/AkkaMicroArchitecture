package as.ama

import com.typesafe.config.Config

/**
 * Configuration read from JSON (HOCON) file.
 */
object AkkaConfig {

  final val actorSystemNameConfigKey = "actorSystemName"

  def apply(config: Config): AkkaConfig = {
    val actorSystemName = config.getString(actorSystemNameConfigKey)

    new AkkaConfig(actorSystemName)
  }
}

case class AkkaConfig(actorSystemName: String) extends Serializable
