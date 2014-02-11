package as.ama

import akka.actor.ActorRef

object AmaMessages {
  case class InitialConfiguration(commandLineArguments: Array[String], amaConfig: AmaConfig, broadcaster: ActorRef) extends Serializable
}
