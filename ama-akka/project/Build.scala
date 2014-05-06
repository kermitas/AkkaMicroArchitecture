import sbt._
import Keys._

object Build extends Build {
  lazy val projectSettings = Defaults.defaultSettings ++ Seq(
    name := "ama-akka",
    version := "0.4.6",
    organization := "as"
  ) ++ ScalaSettings.projectSettings ++ AkkaSettings.projectSettings ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama-akka",
      base = file("."),
      settings = projectSettings
    )

}

object ScalaSettings {
  lazy val projectSettings = Seq(
    scalaVersion := "2.11.0",
    crossScalaVersions := Seq("2.10.4", "2.11.0"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    incOptions := incOptions.value.withNameHashing(true)//,

    //resolvers += Classpaths.typesafeReleases,
    //resolvers += Classpaths.typesafeSnapshots
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
