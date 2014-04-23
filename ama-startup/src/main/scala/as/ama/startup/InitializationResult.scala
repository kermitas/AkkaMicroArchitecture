package as.ama.startup

/**
 * Notification that actor was correctly or incorrectly initialized.
 *
 * Should be send to broadcaster by automatically initialized actor as soon as possible.
 *
 * Correctly initialized actor should publish InitializationResult(Right(...)) on broadcaster.
 *
 * Incorrectly initialized actor should publish InitializationResult(Left(exception)) on broadcaster.
 * If it will not publish timeout will be reached.
 */
case class InitializationResult(result: Either[Exception, Option[Any]]) extends Serializable