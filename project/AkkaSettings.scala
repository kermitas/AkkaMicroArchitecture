import sbt._
import Keys._

object AkkaSettings {
  def apply() = Seq(
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.8" % "provided"
  )
}
