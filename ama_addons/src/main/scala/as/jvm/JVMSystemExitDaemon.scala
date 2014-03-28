package as.jvm

/**
 * Will execute System.exit after delay.
 *
 * @param delayInMs time to wait before System.exit execution
 * @param exitCode exit code passed to System.exit
 */
class JVMSystemExitDaemon(delayInMs: Int, exitCode: Int) extends Thread {
  setDaemon(true)

  override def run = {
    println(s"${classOf[JVMSystemExitDaemon].getSimpleName}: will shut down JVM in ${delayInMs}ms with exit code $exitCode")
    Thread.sleep(delayInMs)
    println(s"${classOf[JVMSystemExitDaemon].getSimpleName}: ${delayInMs}ms is over, shutting down JVM with exit code $exitCode.")
    System.exit(exitCode)

  }
}
