import sbt._
import Keys._

object AmaCoreProject {

  lazy final val projectName = AmaProject.projectName + "-core"

  def apply(version: String, amaAddons: Project) =
    Project(
      id           = projectName,
      base         = file(projectName),

      aggregate    = Seq(amaAddons),
      dependencies = Seq(amaAddons),
      delegates    = Seq(amaAddons),

      settings     = CommonSettings(projectName, version)

    )
}
