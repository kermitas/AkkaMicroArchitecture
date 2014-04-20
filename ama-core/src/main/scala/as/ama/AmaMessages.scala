package as.ama

import akka.actor.ActorRef

object AmaMessages {

  /**
   * This message will be published on broadcaster. Usually StartupInitializer should pick it up and proceed whole
   * initialization process.
   *
   * @param commandLineArguments
   * @param amaConfig
   * @param broadcaster
   */
  case class InitialConfiguration(commandLineArguments: Array[String], amaConfig: AmaConfig, broadcaster: ActorRef) extends Serializable
}
