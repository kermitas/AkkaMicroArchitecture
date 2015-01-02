import sbt.Keys._
import sbt._

object AkkaUtilsSettings {
  def apply() = Seq(
    libraryDependencies += "as.akkautils" %% "akkautils" % "0.1.0-SNAPSHOT"
  )
}
