import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._
import xerial.sbt.Pack._

object Build extends Build {

  lazy val mc = "as.ama.Main"

  lazy val projectSettings = Defaults.defaultSettings ++ packSettings ++ Seq (
    name := "ama-sample",
    version := "0.1.0",
    organization := "as.ama",
    scalaVersion := "2.10.3",
    mainClass in (Compile,run) := Some(mc),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Sonatype Repository" at "http://oss.sonatype.org/content/repositories/releases/",
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    resolvers += "sbt-pack repository" at "http://repo1.maven.org/maven2/",
    libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.0-RC2",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13",
    packMain := Map("run" -> mc)
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

  lazy val amaSample = Project(
      id = "ama-sample",
      base = file("."),
      settings = projectSettings
    ).dependsOn(amaCore, amaStartup, amaAkka).aggregate(amaCore, amaStartup, amaAkka)

  lazy val amaCore = RootProject(file("../ama-core"))
  lazy val amaStartup = RootProject(file("../ama-startup"))
  lazy val amaAkka = RootProject(file("../ama-akka"))
}
