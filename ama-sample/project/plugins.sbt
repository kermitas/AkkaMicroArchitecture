// ===============

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")

// ===============

resolvers += "sbt-pack repository" at "http://repo1.maven.org/maven2/"

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.5.1")

// I am not sure but this line helps to avoid macro errors thrown by SBT while opening project.
// In this line I include scala-reflect again because sbt-pack uses older Scala (that is how I understand this).

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

// ===============

addSbtPlugin("com.danieltrinh" % "sbt-scalariform" % "1.3.0-SNAPSHOT")

// ===============

