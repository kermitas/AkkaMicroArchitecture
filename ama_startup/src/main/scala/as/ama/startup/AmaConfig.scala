package as.ama.startup

import com.typesafe.config.Config
import akka.actor.ActorRef

/**
 * @param commandLineArguments entered as arguments to program or defined in configuration (reference.conf or application.conf)
 * @param config configuration for this single actor, defined in configuration (reference.conf or application.conf)
 * @param broadcaster main, pub-sub communication bus
 */
case class AmaConfig(commandLineArguments: Array[String], config: Config, broadcaster: ActorRef, runtimeProperties: Map[String, Any]) extends Serializable
