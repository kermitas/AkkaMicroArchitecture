package as.ama.startup

import com.typesafe.config.Config
import akka.actor.{ Actor, Props, ActorRef }

class PropsCreator(clazzName: String, commandLineArguments: Array[String], config: Config, broadcaster: ActorRef) extends Serializable {
  def create: Props = Props(Class.forName(clazzName).getConstructor(classOf[Array[String]], classOf[Config], classOf[ActorRef]).newInstance(commandLineArguments, config, broadcaster).asInstanceOf[Actor])
}