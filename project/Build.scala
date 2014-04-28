import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Defaults.defaultSettings ++  Seq(
    name := "ama-all",
    version := "0.4.5",
    organization := "as",
    scalaVersion := "2.11.0",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
  )

  lazy val root = Project(
      id = "akkamicroarchitecture", // name should be the same as folder to be 100% SBT like
      base = file("."),
      settings = projectSettings
    ).aggregate(ama_core).dependsOn(ama_core)

  lazy val ama_core = RootProject(file("ama-core"))
}
