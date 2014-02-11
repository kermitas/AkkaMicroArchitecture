package as.io

/**
 * This class is used to notify that key was pressed.
 */
trait ExecuteOnKeyPress extends Serializable {

  /**
   * This callback method will be executed once key was pressed.
   *
   * Entered text will be passed as argument.
   *
   * @param inputText text entered to console
   * @return indicate if wait for next key press (true) or finish work (false)
   */
  def keyWasPressedNotification(inputText: String): Boolean
}
