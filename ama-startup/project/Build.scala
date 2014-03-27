import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Seq (
    name := "ama-startup",
    version := "0.4.1",
    organization := "as.ama",
    scalaVersion := "2.10.3",
    offline := true,
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
  ) ++ ScalariformSettings.projectSettings

  lazy val amaStartup = Project(
      id = "ama-startup",
      base = file("."),
      settings = projectSettings
    ).aggregate(amaAkka).dependsOn(amaAkka)

  lazy val amaAkka = RootProject(file("../ama-akka"))
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