import sbt._
import Keys._

object Build extends Build {

  lazy val mc = "as.ama.Main"

  lazy val projectSettings = Seq (
    name := "ama-sample",
    version := "0.4.1",
    organization := "as.ama",
    scalaVersion := "2.10.3",
    offline := true,
    mainClass in (Compile,run) := Some(mc),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    resolvers += Classpaths.typesafeReleases,
    resolvers += Classpaths.typesafeSnapshots,
    libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.0",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.1"
  ) ++ PackSettings.projectSettings(mc) ++ ScalariformSettings.projectSettings

  lazy val amaSample = Project(
      id = "ama-sample",
      base = file("."),
      settings = projectSettings
    ).dependsOn(amaCore, amaStartup, amaAkka).aggregate(amaCore, amaStartup, amaAkka)

  lazy val amaCore = RootProject(file("../ama-core"))
  lazy val amaStartup = RootProject(file("../ama-startup"))
  lazy val amaAkka = RootProject(file("../ama-akka"))
}

object PackSettings {

  import xerial.sbt.Pack._

  def projectSettings(mainClass: String) = {
    packSettings ++ Seq (
      packMain := Map("run" -> mainClass)
    )
  }
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