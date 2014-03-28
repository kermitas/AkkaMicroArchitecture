import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Seq (
    name := "ama-all",
    version := "0.4.1",
    organization := "as"
  )

  lazy val root = Project(
      id = "ama_all",
      base = file("."),
      settings = projectSettings
    ).aggregate(ama_core).dependsOn(ama_core)

  lazy val ama_core = RootProject(file("ama_core"))
}
