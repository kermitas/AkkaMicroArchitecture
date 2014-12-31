import sbt._
import Keys._

object AmaAkkaProject {

  lazy final val projectName = AmaProject.projectName + "-akka"

  def apply(version: String) =
    Project(
      id       = projectName,
      base     = file(projectName),

      settings = CommonSettings(projectName, version) ++
                 AkkaSettings()
    )
}
