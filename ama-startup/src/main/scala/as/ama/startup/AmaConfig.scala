package as.ama.startup

import com.typesafe.config.Config
import akka.actor.ActorRef

/**
 * @param commandLineArguments entered as arguments to program or defined in configuration (reference.conf or application.conf)
 * @param config configuration for this single actor, defined in configuration (reference.conf or application.conf)
 * @param broadcaster main, pub-sub communication bus
 * @param initializationResultListener here Initialization result should be send as soon as possible (otherwise timeout will be reached (equivalent of InitializationResult(Left( timeout exception ) )
 */
class AmaConfig(val commandLineArguments: Array[String], val config: Config, val broadcaster: ActorRef, val initializationResultListener: ActorRef) extends Serializable
