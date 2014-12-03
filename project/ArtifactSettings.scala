import sbt.Keys._
import sbt.Package.ManifestAttributes

object ArtifactSettings {
  def apply(artifactName: String, projectVersion: String) = Seq(
    name           := artifactName,
    version        := projectVersion,
    organization   := "as.ama"
  )
}


