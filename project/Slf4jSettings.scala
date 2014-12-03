import sbt._
import Keys._

object Slf4jSettings {
  def apply() = Seq(
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.7"
  )
}
