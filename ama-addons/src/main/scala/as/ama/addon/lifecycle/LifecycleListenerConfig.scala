package as.ama.addon.lifecycle

import com.typesafe.config.Config

/**
 * Configuration read from JSON (HOCON) file.
 */
object LifecycleListenerConfig {

  final val automaticallyShutdownJVMAfterTimeInMsConfigKey = "automaticallyShutdownJVMAfterTimeInMs"
  final val automaticallyKillJVMAfterShutdownTryTimeInMsConfigKey = "automaticallyKillJVMAfterShutdownTryTimeInMs"
  final val onShutdownWaitForEventBusMessagesToBeProcessedInMsConfigKey = "onShutdownWaitForEventBusMessagesToBeProcessedInMs"

  def apply(config: Config): LifecycleListenerConfig = {
    val automaticallyShutdownJVMAfterTimeInMs = config.getInt(automaticallyShutdownJVMAfterTimeInMsConfigKey)
    val automaticallyKillJVMAfterShutdownTryTimeInMs = config.getInt(automaticallyKillJVMAfterShutdownTryTimeInMsConfigKey)
    val onShutdownWaitForEventBusMessagesToBeProcessedInMs = config.getInt(onShutdownWaitForEventBusMessagesToBeProcessedInMsConfigKey)

    new LifecycleListenerConfig(automaticallyShutdownJVMAfterTimeInMs, automaticallyKillJVMAfterShutdownTryTimeInMs, onShutdownWaitForEventBusMessagesToBeProcessedInMs)
  }
}

case class LifecycleListenerConfig(automaticallyShutdownJVMAfterTimeInMs: Int, automaticallyKillJVMAfterShutdownTryTimeInMs: Int, onShutdownWaitForEventBusMessagesToBeProcessedInMs: Int) extends Serializable