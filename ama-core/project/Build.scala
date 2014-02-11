import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._

object Build extends Build {

  lazy val projectSettings = Defaults.defaultSettings ++ Seq (
    name := "ama-core",
    version := "0.4.0",
    organization := "as.ama",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
  ) ++ scalariformSettings ++ formattingPreferences

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    ScalariformKeys.preferences := FormattingPreferences()
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)
  }

  lazy val amaCore = Project(
      id = "ama-core",
      base = file("."),
      settings = projectSettings
    ).aggregate(amaAddons).dependsOn(amaAddons)

  lazy val amaAddons = RootProject(file("../ama-addons"))

}
