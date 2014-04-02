package as.ama

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import akka.actor._
import akka.pattern.ask
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

  val amaRootActorNumber = new java.util.concurrent.atomic.AtomicLong(0)

  def main(commandLineArguments: Array[String]) {

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

    val amaRootActorInitializationTimeoutInSeconds = 10 // this time is not so important here since we are doing nothing with returned future

    startNewAmaRootActor(actorSystem, amaConfig, cmdArgs, runtimePropertiesBuilder, amaRootActorInitializationTimeoutInSeconds, None)
  }

  /**
   * Having those arguments you can reuse this method to build new broadcaster-based systems on demand in given actorSystem
   *
   * Returns future with reference to newly created broadcaster.
   */
  def startNewAmaRootActor(actorSystem: ActorSystem, amaConfig: AmaConfig, cmdArgs: Array[String], runtimePropertiesBuilder: RuntimePropertiesBuilder, amaRootActorInitializationTimeoutInSeconds: Int, executeWithBroadcaster: Option[ExecuteWithBroadcaster]): Future[ActorRef] = {
    val rootActorNumber = amaRootActorNumber.getAndIncrement
    val amaRootActor = actorSystem.actorOf(Props[AmaRootActor], classOf[AmaRootActor].getSimpleName + "-" + rootActorNumber)
    val initMessage = new AmaRootActor.Init(amaConfig, cmdArgs, runtimePropertiesBuilder, executeWithBroadcaster)
    amaRootActor.ask(initMessage)(amaRootActorInitializationTimeoutInSeconds seconds).mapTo[ActorRef]
  }

  protected def prepareCommandLineArguments(originallyPassedCommandLineArguments: Array[String], commandLineConfig: CommandLineConfig): Array[String] = {

    if (commandLineConfig.overrideOriginallyPassedArguments) {
      println(s"Overwriting command line arguments with '${commandLineConfig.arguments}'")
      new scala.util.matching.Regex(commandLineArgumentsRegex).findAllIn(commandLineConfig.arguments.trim).toArray
    } else
      originallyPassedCommandLineArguments
  }
}