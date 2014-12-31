import sbt._
import Keys._

object ScalaTestSettings {
  def apply() = Seq(
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  )
}
