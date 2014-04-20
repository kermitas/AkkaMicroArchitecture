package as.akka.util

import akka.actor.{ Props, ActorContext, ActorRef }

class CreateActorAndSendMessageExecuteInActorsContext(props: Props, name: String, message: Any, messageSender: ActorRef) extends CreateActorExecuteInActorsContext(props, name) {
  override def execute(context: ActorContext): ActorRef = {
    val createdActor = super.execute(context)
    createdActor.tell(message, messageSender)
    createdActor
  }
}