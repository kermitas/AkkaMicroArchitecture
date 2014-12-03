import sbt._
import Keys._

object AmaStartupProject {

  lazy final val projectName = AmaAllProject.projectName + "-startup"

  def apply(version: String, amaAkka: Project) =
    Project(
      id           = projectName,
      base         = file(projectName),

      aggregate    = Seq(amaAkka),
      dependencies = Seq(amaAkka),
      delegates    = Seq(amaAkka),

      settings     = CommonSettings(projectName, version)
    )
}
