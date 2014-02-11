package as.ama

import com.typesafe.config.Config

/**
 * Configuration read from JSON (HOCON) file.
 */
object CommandLineConfig {

  final val overrideOriginallyPassedArgumentsConfigKey = "overrideOriginallyPassedArguments"
  final val argumentsConfigKey = "arguments"

  def apply(config: Config): CommandLineConfig = {
    val overrideOriginallyPassedArguments = config.getBoolean(overrideOriginallyPassedArgumentsConfigKey)
    val arguments = config.getString(argumentsConfigKey)

    new CommandLineConfig(overrideOriginallyPassedArguments, arguments)
  }
}

case class CommandLineConfig(overrideOriginallyPassedArguments: Boolean, arguments: String) extends Serializable