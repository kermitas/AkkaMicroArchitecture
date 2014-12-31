import sbt._
import Keys._

object AkkaSlf4JSettings {
  def apply() = Seq(
    libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.7"
  )
}
