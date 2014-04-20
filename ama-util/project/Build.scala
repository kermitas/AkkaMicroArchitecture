import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Seq(
    name := "ama-util",
    version := "0.4.5",
    organization := "as",
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
  ) ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama-util",
      base = file("."),
      settings = projectSettings
    ).aggregate(ama_startup).dependsOn(ama_startup)

  lazy val ama_startup = RootProject(file("../ama-startup"))

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
