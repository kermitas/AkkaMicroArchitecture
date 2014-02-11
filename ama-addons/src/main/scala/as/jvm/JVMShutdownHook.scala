package as.jvm

import akka.actor.ActorRef

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
