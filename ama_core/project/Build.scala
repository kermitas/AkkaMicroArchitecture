import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Seq(
    name := "ama-core",
    version := "0.4.2",
    organization := "as",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
  ) ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama_core",
      base = file("."),
      settings = projectSettings
    ).aggregate(ama_addons).dependsOn(ama_addons)

  lazy val ama_addons = RootProject(file("../ama_addons"))

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
