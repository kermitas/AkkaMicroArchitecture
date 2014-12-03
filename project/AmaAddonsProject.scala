import sbt._
import Keys._

object AmaAddonsProject {

  lazy final val projectName = AmaAllProject.projectName + "-addons"

  def apply(version: String, amaStartup: Project) =
    Project(
      id           = projectName,
      base         = file(projectName),

      aggregate    = Seq(amaStartup),
      dependencies = Seq(amaStartup),
      delegates    = Seq(amaStartup),

      settings     = CommonSettings(projectName, version)
    )
}
