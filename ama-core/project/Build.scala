import sbt._
import Keys._

object Build extends Build {
  lazy val projectSettings = Seq(
    name := "ama-core",
    version := "0.4.7",
    organization := "as"
  ) ++ ScalaSettings.projectSettings ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama-core",
      base = file("."),
      settings = projectSettings
    ).aggregate(ama_addons).dependsOn(ama_addons)

  lazy val ama_addons = RootProject(file("../ama-addons"))
}

object ScalaSettings {
  lazy val projectSettings = Seq(
    scalaVersion := "2.11.2",
    crossScalaVersions := Seq("2.10.4", "2.11.2"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    incOptions := incOptions.value.withNameHashing(true)
  )
}

object ScalariformSettings {
  lazy val projectSettings = {
    import com.typesafe.sbt.SbtScalariform._
    import scalariform.formatter.preferences._

    scalariformSettings ++ {
      ScalariformKeys.preferences := FormattingPreferences()
        .setPreference(AlignParameters, true)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
        .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)
    }
  }
}
