package as.ama.akka

import akka.actor.{ Props, ActorContext, ActorRef }

/**
 * This class can be used to send as message to other actor that will create actor in its context (of course to do that
 * it has to receive ExecuteInActorsContext message).
 */
class CreateActorExecuteInActorsContext(props: Props, name: String) extends ExecuteInActorsContext {
  override def execute(context: ActorContext): ActorRef = context.actorOf(props, name)
}