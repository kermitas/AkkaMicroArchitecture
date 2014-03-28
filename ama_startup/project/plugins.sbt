// ===============

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")

// ===============

resolvers += Classpaths.sbtPluginSnapshots

addSbtPlugin("com.danieltrinh" % "sbt-scalariform" % "1.3.0-SNAPSHOT")

// ===============
