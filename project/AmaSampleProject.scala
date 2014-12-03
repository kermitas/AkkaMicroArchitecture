import sbt._
import Keys._

object AmaSampleProject {

  lazy final val projectName                 = AmaAllProject.projectName + "-sample"
  lazy final val mainClassFullyQualifiedName = "as.ama.Main"

  def apply(version: String, amaAll: Project) =
    Project(
      id           = projectName,
      base         = file(projectName),

      aggregate    = Seq(amaAll),
      dependencies = Seq(amaAll),
      delegates    = Seq(amaAll),

      settings     = CommonSettings(projectName, version) ++
                     AkkaSlf4JSettings() ++
                     Slf4jSettings() ++
                     mainClassSettings(mainClassFullyQualifiedName) ++
                     PackSettings(mainClassFullyQualifiedName) ++
                     AssemblySettings(mainClassFullyQualifiedName)
    )

  protected def mainClassSettings(mainClassFullyQualifiedName: String) = Seq (
    mainClass in (Compile,run) := Some(mainClassFullyQualifiedName)
  )
}
