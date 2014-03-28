import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Seq (
    name := "ama-startup",
    version := "0.4.1",
    organization := "as",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
  ) ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama_startup",
      base = file("."),
      settings = projectSettings
    ).aggregate(ama_akka).dependsOn(ama_akka)

  lazy val ama_akka = RootProject(file("../ama_akka"))
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
