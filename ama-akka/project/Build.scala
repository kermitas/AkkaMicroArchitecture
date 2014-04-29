import sbt._
import Keys._

object Build extends Build {

  lazy val projectSettings = Defaults.defaultSettings ++ Seq(
    name := "ama-akka",
    version := "0.4.5",
    organization := "as",
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    incOptions := incOptions.value.withNameHashing(true),
    resolvers += Classpaths.typesafeReleases,
    resolvers += Classpaths.typesafeSnapshots
  ) ++ AkkaSettings.projectSettings ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama-akka",
      base = file("."),
      settings = projectSettings
    )

}

object AkkaSettings {

  lazy val projectSettings = Seq(
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.2"
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
