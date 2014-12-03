import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings =  Seq(
    name := "ama-all",
    version := "0.4.7",
    organization := "as"
  ) ++ ScalaSettings.projectSettings

  lazy val root = Project(
      id = "akkamicroarchitecture", // name should be the same as folder to be 100% SBT like
      base = file("."),
      settings = projectSettings
    ).aggregate(ama_core).dependsOn(ama_core)

  lazy val ama_core = RootProject(file("ama-core"))
}

object ScalaSettings {
  lazy val projectSettings = Seq(
    scalaVersion := "2.11.2",
    crossScalaVersions := Seq("2.10.4", "2.11.2"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    incOptions := incOptions.value.withNameHashing(true)
  )
}