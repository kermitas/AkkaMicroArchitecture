import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Seq (
    name := "ama-akka",
    version := "0.4.1",
    organization := "as.ama",
    scalaVersion := "2.10.3",
    offline := true,
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    resolvers += Classpaths.typesafeReleases,
    resolvers += Classpaths.typesafeSnapshots,
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.0"
  ) ++ ScalariformSettings.projectSettings

  lazy val amaAkka = Project(
      id = "ama-akka",
      base = file("."),
      settings = projectSettings
    )
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