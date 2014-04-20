package as.ama.addon.lifecycle

import com.typesafe.config.Config

/**
 * Configuration read from JSON (HOCON) file.
 */
object LifecycleManagerConfig {

  final val automaticallyShutdownJVMAfterTimeInMsConfigKey = "automaticallyShutdownJVMAfterTimeInMs"
  final val automaticallyKillJVMAfterShutdownTryTimeInMsConfigKey = "automaticallyKillJVMAfterShutdownTryTimeInMs"
  final val onShutdownWaitForEventBusMessagesToBeProcessedInMsConfigKey = "onShutdownWaitForEventBusMessagesToBeProcessedInMs"

  def apply(config: Config): LifecycleManagerConfig = {
    val automaticallyShutdownJVMAfterTimeInMs = config.getInt(automaticallyShutdownJVMAfterTimeInMsConfigKey)
    val automaticallyKillJVMAfterShutdownTryTimeInMs = config.getInt(automaticallyKillJVMAfterShutdownTryTimeInMsConfigKey)
    val onShutdownWaitForEventBusMessagesToBeProcessedInMs = config.getInt(onShutdownWaitForEventBusMessagesToBeProcessedInMsConfigKey)

    new LifecycleManagerConfig(automaticallyShutdownJVMAfterTimeInMs, automaticallyKillJVMAfterShutdownTryTimeInMs, onShutdownWaitForEventBusMessagesToBeProcessedInMs)
  }
}

case class LifecycleManagerConfig(automaticallyShutdownJVMAfterTimeInMs: Int, automaticallyKillJVMAfterShutdownTryTimeInMs: Int, onShutdownWaitForEventBusMessagesToBeProcessedInMs: Int) extends Serializable