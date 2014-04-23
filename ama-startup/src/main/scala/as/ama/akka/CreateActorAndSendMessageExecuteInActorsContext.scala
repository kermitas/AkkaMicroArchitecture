package as.ama.akka

import akka.actor.{ Props, ActorContext, ActorRef }

/**
 * Will create actor under context of other actor and then will forward message to newly created actor.
 */
class CreateActorAndSendMessageExecuteInActorsContext(props: Props, name: String, message: Any, messageSender: ActorRef) extends CreateActorExecuteInActorsContext(props, name) {
  override def execute(context: ActorContext): ActorRef = {
    val createdActor = super.execute(context)
    createdActor.tell(message, messageSender)
    createdActor
  }
}