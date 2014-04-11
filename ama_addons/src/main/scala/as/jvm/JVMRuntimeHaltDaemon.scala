package as.jvm

/**
 * Will execute Runtime.halt after delay.
 *
 * @param delayInMs time to wait before Runtime.halt execution
 * @param exitCode exit code passed to Runtime.halt
 */
class JVMRuntimeHaltDaemon(delayInMs: Int, exitCode: Int) extends Thread {
  setDaemon(true)

  override def run = {
    println(s"${getClass.getSimpleName}: will kill JVM in ${delayInMs}ms with exit code $exitCode")
    Thread.sleep(delayInMs)
    println(s"${getClass.getSimpleName}: ${delayInMs}ms is over, killing JVM with exit code $exitCode.")
    Runtime.getRuntime.halt(exitCode)
  }
}