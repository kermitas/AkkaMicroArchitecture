package as.ama

import com.typesafe.config.Config
import as.ama.startup.InitializeOnStartupConfig

/**
 * Configuration read from JSON (HOCON) file.
 */
object AmaConfig {

  final val renderConfigurationConfigKey = "renderConfiguration"
  final val commandLineConfigKey = "commandLine"
  //final val akkaConfigKey = "akka"
  final val initializeOnStartupConfigKey = "initializeOnStartup"
  final val logMessagesPublishedOnBroadcasterConfigKey = "logMessagesPublishedOnBroadcaster"

  def apply(config: Config): AmaConfig = {
    val renderConfiguration = if (config.hasPath(renderConfigurationConfigKey)) config.getBoolean(renderConfigurationConfigKey) else false
    val commandLineConfig = CommandLineConfig(config.getConfig(commandLineConfigKey))

    //val akkaConfig = AkkaConfig(config.getConfig(akkaConfigKey))
    val initializeOnStartupConfig = InitializeOnStartupConfig(config.getConfig(initializeOnStartupConfigKey))

    val logMessagesPublishedOnBroadcaster = config.getBoolean(logMessagesPublishedOnBroadcasterConfigKey)

    new AmaConfig(renderConfiguration, commandLineConfig, initializeOnStartupConfig, logMessagesPublishedOnBroadcaster)
  }
}

case class AmaConfig(renderConfiguration: Boolean, commandLineConfig: CommandLineConfig, initializeOnStartupConfig: InitializeOnStartupConfig, logMessagesPublishedOnBroadcaster: Boolean) extends Serializable
