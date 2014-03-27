import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Seq (
    name := "ama-addons",
    version := "0.4.1",
    organization := "as.ama",
    scalaVersion := "2.10.3",
    offline := true,
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
  ) ++ ScalariformSettings.projectSettings

  lazy val amaAddons = Project(
      id = "ama-addons",
      base = file("."),
      settings = projectSettings
    ).aggregate(amaStartup).dependsOn(amaStartup)

  lazy val amaStartup = RootProject(file("../ama-startup"))
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