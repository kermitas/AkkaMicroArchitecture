package as.akka.util

import akka.actor.{ ActorContext, ActorRef }

/**
 * Used in AmaRootActor (ama-core) to create actors in its context.
 */
trait ExecuteInActorsContext extends Serializable {
  def execute(context: ActorContext): ActorRef
}