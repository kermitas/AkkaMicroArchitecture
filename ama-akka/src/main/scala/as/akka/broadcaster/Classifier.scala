package as.akka.broadcaster

import akka.actor._

trait Classifier extends Serializable {
  def map(a: Any, sender: ActorRef): Option[Any]
}
