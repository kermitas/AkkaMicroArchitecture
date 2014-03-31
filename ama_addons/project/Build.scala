import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Seq(
    name := "ama-addons",
    version := "0.4.2",
    organization := "as",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
  ) ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama_addons",
      base = file("."),
      settings = projectSettings
    ).aggregate(ama_startup).dependsOn(ama_startup)

  lazy val ama_startup = RootProject(file("../ama_startup"))
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
