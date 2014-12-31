import sbt._
import Keys._

object AmaProject {

  lazy final val projectName = "ama"

  def apply(version: String, amaCore: Project) =
    Project(
      id           = projectName,
      base         = file("."),

      aggregate    = Seq(amaCore),
      dependencies = Seq(amaCore),
      delegates    = Seq(amaCore),

      settings     = CommonSettings(projectName, version)
    )
}
