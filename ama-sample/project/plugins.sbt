// ===============

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")

// ===============

resolvers += "sbt-pack repository" at "http://repo1.maven.org/maven2/"

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.4.2")

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.3"

// ===============

resolvers += Classpaths.sbtPluginSnapshots

addSbtPlugin("com.danieltrinh" % "sbt-scalariform" % "1.3.0-SNAPSHOT")

// ===============

