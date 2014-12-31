import sbt._
import Keys._

object LogbackClassicSettings {
  def apply() = Seq(
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"
  )
}
