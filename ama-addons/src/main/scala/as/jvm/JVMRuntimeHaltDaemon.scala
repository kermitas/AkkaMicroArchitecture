package as.jvm

class JVMRuntimeHaltDaemon(delayInMs: Int, exitCode: Int) extends Thread {
  setDaemon(true)

  override def run = {
    println(s"${classOf[JVMRuntimeHaltDaemon].getSimpleName}: will kill JVM in ${delayInMs}ms with exit code $exitCode")
    Thread.sleep(delayInMs)
    println(s"${classOf[JVMRuntimeHaltDaemon].getSimpleName}: ${delayInMs}ms is over, killing JVM with exit code $exitCode.")
    System.exit(exitCode)

  }
}