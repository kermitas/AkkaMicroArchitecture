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

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.5")

// ===============
