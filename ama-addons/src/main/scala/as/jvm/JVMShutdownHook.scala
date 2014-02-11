package as.jvm

import akka.actor.ActorRef

/**
 * Will send 'message' to 'receiver' actor on JVM shutdown.
 *
 * This class should be register as JVM shutdown hook.
 *
 * @param receiver the received actor (to which message will be send)
 * @param message message that will be send to receiver actor
 * @param keepHookForTimeInMs will keep hook for some time (so you can have a time to finish other actions)
 */
class JVMShutdownHook(receiver: ActorRef, message: AnyRef, keepHookForTimeInMs: Int) extends Thread {
  override def run = {
    try {
      println(s"${classOf[JVMShutdownHook].getSimpleName}: JVM shutdown signal detected! Will keep shutdown hook for ${keepHookForTimeInMs}ms while system will be shutting down...")
      receiver ! message
    } finally {
      Thread.sleep(keepHookForTimeInMs)
      println(s"${classOf[JVMShutdownHook].getSimpleName}: ${keepHookForTimeInMs}ms is over, releasing shutdown hook, bye!")
    }
  }
}
