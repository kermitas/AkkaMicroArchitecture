import sbt._
import Keys._

object AmaSampleProject {

  lazy final val projectName                 = AmaProject.projectName + "-sample"
  lazy final val mainClassFullyQualifiedName = "as.ama.Main"

  def apply(version: String, ama: Project) =
    Project(
      id           = projectName,
      base         = file(projectName),

      aggregate    = Seq(ama),
      dependencies = Seq(ama),
      delegates    = Seq(ama),

      settings     = CommonSettings(projectName, version) ++
                     AkkaSlf4JSettings() ++
                     LogbackClassicSettings() ++
                     mainClassSettings(mainClassFullyQualifiedName) ++
                     PackSettings(mainClassFullyQualifiedName) ++
                     AssemblySettings(mainClassFullyQualifiedName)
    )

  protected def mainClassSettings(mainClassFullyQualifiedName: String) = Seq (
    mainClass in (Compile,run) := Some(mainClassFullyQualifiedName)
  )
}
