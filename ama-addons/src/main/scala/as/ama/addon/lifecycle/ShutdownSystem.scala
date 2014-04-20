package as.ama.addon.lifecycle

case class ShutdownSystem(reason: Either[Exception, String]) extends Serializable