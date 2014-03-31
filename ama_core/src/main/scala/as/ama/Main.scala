package as.ama

import akka.actor._
import com.typesafe.config._
import as.ama.startup.RuntimePropertiesBuilder

/**
 * Main JVM class.
 *
 * Responsibilities:
 * - read configuration (and log it when needed)
 * - use original arguments passed from command line or those overwritten by those in configuration
 * - start actor system
 * - start MainActor
 * - send initial message to MainActor
 */
object Main {

  final val mainConfigKey = "ama"
  final val renderConfigurationConfigKey = "renderConfiguration"
  final val commandLineArgumentsRegex = "\"(\\\"|[^\"])*?\"|[^\\s]+"

  def main(commandLineArguments: Array[String]) = {

    println("Reading configuration...")
    val config = ConfigFactory.load

    val amaBareConfig = config.getConfig(mainConfigKey)

    if (amaBareConfig.hasPath(renderConfigurationConfigKey) && amaBareConfig.getBoolean(renderConfigurationConfigKey)) println(s"Configuration: ${config.root.render}")

    val amaConfig = AmaConfig(amaBareConfig)

    // TODO: add initialization fail watchdog that will stop this JVM when not cancelled (timeout should be defined in config)

    val cmdArgs = prepareCommandLineArguments(commandLineArguments, amaConfig.commandLineConfig)

    println("Starting actor system...")
    val actorSystem = ActorSystem(amaConfig.akkaConfig.actorSystemName, config)

    val runtimePropertiesBuilder: RuntimePropertiesBuilder = Class.forName(amaConfig.initializeOnStartupConfig.runtimePropertiesBuilderClassName).getConstructor().newInstance().asInstanceOf[RuntimePropertiesBuilder]

    createMainActorAndSendInit(actorSystem, amaConfig, cmdArgs, runtimePropertiesBuilder)
  }

  /**
   * Having those arguments you can reuse this method to build new broadcaster-based systems on demand in given actorSystem
   */
  def createMainActorAndSendInit(actorSystem: ActorSystem, amaConfig: AmaConfig, cmdArgs: Array[String], runtimePropertiesBuilder: RuntimePropertiesBuilder) {

    println(s"Creating ${classOf[MainActor].getSimpleName}...")
    val mainActor = actorSystem.actorOf(Props[MainActor], classOf[MainActor].getSimpleName)

    println("Starting main actor...")
    mainActor ! new MainActor.Init(amaConfig, cmdArgs, runtimePropertiesBuilder)
  }

  protected def prepareRuntimePropertiesBuilder = {

  }

  protected def prepareCommandLineArguments(originallyPassedCommandLineArguments: Array[String], commandLineConfig: CommandLineConfig): Array[String] = {

    if (commandLineConfig.overrideOriginallyPassedArguments) {
      println(s"Overwriting command line arguments with '${commandLineConfig.arguments}'")
      new scala.util.matching.Regex(commandLineArgumentsRegex).findAllIn(commandLineConfig.arguments.trim).toArray
    } else
      originallyPassedCommandLineArguments
  }
}