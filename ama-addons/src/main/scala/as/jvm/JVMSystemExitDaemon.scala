package as.jvm

class JVMSystemExitDaemon(delayInMs: Int, exitCode: Int) extends Thread {
  setDaemon(true)

  override def run = {
    println(s"${classOf[JVMSystemExitDaemon].getSimpleName}: will shut down JVM in ${delayInMs}ms with exit code $exitCode")
    Thread.sleep(delayInMs)
    println(s"${classOf[JVMSystemExitDaemon].getSimpleName}: ${delayInMs}ms is over, shutting down JVM with exit code $exitCode.")
    System.exit(exitCode)

  }
}
