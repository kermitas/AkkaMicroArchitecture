/**
 * sbt-pack settings.
 */
object PackSettings {

  import xerial.sbt.Pack._

  def apply(mainClassFullyQualifiedName: String) = {
    packSettings ++ Seq(
      packMain := Map("run" -> mainClassFullyQualifiedName)
    )
  }
}
