import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._

object Build extends Build {

  lazy val projectSettings = Defaults.defaultSettings ++ Seq (
    name := "ama-startup",
    version := "0.1.0",
    organization := "as.ama",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Sonatype Repository" at "http://oss.sonatype.org/content/repositories/releases/",
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
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

  lazy val amaStartup = Project(
      id = "ama-startup",
      base = file("."),
      settings = projectSettings
    ).aggregate(amaAkka).dependsOn(amaAkka)

  lazy val amaAkka = RootProject(file("../ama-akka"))
}
