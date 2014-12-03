import sbt._
import Keys._

object Build extends Build {

  lazy val mc = "as.ama.Main" // here main class is set

  lazy val projectSettings = Seq(
    name := "ama-sample",
    version := "0.4.7",
    organization := "as",
    mainClass in (Compile,run) := Some(mc)
  ) ++ ScalaSettings.projectSettings ++ AkkaSettings.projectSettings ++ PackSettings.projectSettings(mc) ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama-sample",
      base = file("."),
      settings = projectSettings
    ).dependsOn(ama_all).aggregate(ama_all)

  lazy val ama_all = RootProject(file("../"))
}

object ScalaSettings {

  lazy val projectSettings = Seq(
    scalaVersion := "2.11.2",
    crossScalaVersions := Seq("2.10.4", "2.11.2"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    incOptions := incOptions.value.withNameHashing(true),

    resolvers += Classpaths.typesafeReleases,
    resolvers += Classpaths.typesafeSnapshots
  )
}

object AkkaSettings {
  lazy val projectSettings = Seq(
    libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.7",
    //libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.7"
  )
}

object PackSettings {

  import xerial.sbt.Pack._

  def projectSettings(mainClass: String) = {
    packSettings ++ Seq(
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
