import sbt._
import Keys._

object Build extends Build {

  lazy val mc = "as.ama.Main" // here main class is set

  lazy val projectSettings = Seq(
    name := "ama-sample",
    version := "0.4.2",
    organization := "as",
    scalaVersion := "2.10.3",
    mainClass in (Compile,run) := Some(mc),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    resolvers += Classpaths.typesafeReleases,
    resolvers += Classpaths.typesafeSnapshots
  ) ++ AkkaSettings.projectSettings ++ PackSettings.projectSettings(mc) ++ ScalariformSettings.projectSettings

  lazy val root = Project(
      id = "ama_sample",
      base = file("."),
      settings = projectSettings
    ).dependsOn(ama_all).aggregate(ama_all)

  lazy val ama_all = RootProject(file("../"))
}

object AkkaSettings {

  lazy val projectSettings = Seq(
    libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.0",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.1"
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
