package as.ama.startup

/**
 * Notification that actor was correctly or incorrectly initialized.
 *
 * Should be send to initializationResultListener by automatically initialized actor as soon as possible.
 *
 * Correctly initialized actor should send InitializationResult(Right(...)) to initializationResultListener.
 *
 * Incorrectly initialized actor should send InitializationResult(Left(exception)) to initializationResultListener.
 *
 * If it will not send InitializationResult then timeout will be reached.
 */
case class InitializationResult(result: Either[Exception, Option[Any]]) extends Serializable