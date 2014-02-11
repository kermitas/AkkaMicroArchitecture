package as.ama.startup

/**
 * Used inside InitializationResult(Left(h-e-r-e)) when published on broadcaster to indicate that initialization timeout
 * for some actor was reached.
 *
 * @param msg
 */
class InitializationTimeoutException(msg: String) extends RuntimeException(msg)
