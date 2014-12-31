import sbt.Keys._
import sbt._

/**
 * This file contains common settings.
 * Those settings needs to be implemented by all projects.
 */
object CommonSettings {
  def apply(artifactName: String, projectVersion: String) = ArtifactSettings(artifactName, projectVersion) ++
                                                            ScalaSettings() ++
                                                            DependencyGraphSettings() ++
                                                            ScalariformSettings()
}


