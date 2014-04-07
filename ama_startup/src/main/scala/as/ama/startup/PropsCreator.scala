package as.ama.startup

import com.typesafe.config.Config
import akka.actor.{ Actor, Props, ActorRef }

/**
 * Create Props (Akka initializer) needed by actor system to create instance of an actor.
 *
 * @param clazzName class name to instantiate
 * @param commandLineArguments entered as arguments to program or defined in configuration (reference.conf or application.conf)
 * @param config configuration for this single actor, defined in configuration (reference.conf or application.conf)
 * @param broadcaster main, pub-sub communication bus
 */
class PropsCreator(clazzName: String, commandLineArguments: Array[String], config: Config, broadcaster: ActorRef, runtimeProperties: Map[String, Any]) extends Serializable {
  def create: Props = {
    val amaConfig = new AmaConfig(commandLineArguments, config, broadcaster, runtimeProperties)
    Props(Class.forName(clazzName).getConstructor(classOf[AmaConfig]).newInstance(amaConfig).asInstanceOf[Actor])
  }
}