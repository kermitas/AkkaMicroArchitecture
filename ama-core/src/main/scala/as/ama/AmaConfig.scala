package as.ama

import com.typesafe.config.Config
import as.ama.startup.InitializeOnStartupConfig

object AmaConfig {

  final val commandLineConfigKey = "commandLine"
  final val akkaConfigKey = "akka"
  final val initializeOnStartupConfigKey = "initializeOnStartup"
  final val logMessagesPublishedOnBroadcasterConfigKey = "logMessagesPublishedOnBroadcaster"

  def apply(config: Config): AmaConfig = {
    val commandLineConfig = CommandLineConfig(config.getConfig(commandLineConfigKey))
    val akkaConfig = AkkaConfig(config.getConfig(akkaConfigKey))
    val initializeOnStartupConfig = InitializeOnStartupConfig(config.getConfig(initializeOnStartupConfigKey))
    val logMessagesPublishedOnBroadcaster = config.getBoolean(logMessagesPublishedOnBroadcasterConfigKey)

    new AmaConfig(commandLineConfig, akkaConfig, initializeOnStartupConfig, logMessagesPublishedOnBroadcaster)
  }
}

case class AmaConfig(commandLineConfig: CommandLineConfig, akkaConfig: AkkaConfig, initializeOnStartupConfig: InitializeOnStartupConfig, logMessagesPublishedOnBroadcaster: Boolean) extends Serializable
