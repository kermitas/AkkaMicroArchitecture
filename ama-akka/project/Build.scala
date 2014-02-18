import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._

object Build extends Build {

  lazy val projectSettings = Defaults.defaultSettings ++ Seq (
    name := "ama-akka",
    version := "0.4.0",
    organization := "as.ama",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    resolvers += Classpaths.typesafeReleases,
    resolvers += Classpaths.typesafeSnapshots,
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.0-RC3"
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

  lazy val amaAkka = Project(
      id = "ama-akka",
      base = file("."),
      settings = projectSettings
    )
}
