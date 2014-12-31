// This file contains a list of SBT plugins.

// ===============

resolvers += Resolver.sonatypeRepo("snapshots")
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")

// ===============

resolvers += Classpaths.sbtPluginSnapshots
addSbtPlugin("com.danieltrinh" % "sbt-scalariform" % "1.3.0")

// ===============

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5-SNAPSHOT")

// ===============

resolvers += "Maven Central Repository" at "http://repo1.maven.org/maven2/"
addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.6.2")

// In case of SBT problems:
//   I am not sure but this line helps to avoid macro errors thrown by SBT while opening project.
//   In this line I include scala-reflect again because sbt-pack uses older Scala (that is how I understand this).
//
//  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

// ===============

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.5")

// ===============

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

// ===============
