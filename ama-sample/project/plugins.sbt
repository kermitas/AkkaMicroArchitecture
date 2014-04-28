// ===============

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")

// ===============

resolvers += "sbt-pack repository" at "http://repo1.maven.org/maven2/"

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.5.1")

// need to include scala-reflect again because sbt-pack uses older Scala

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.0"

// ===============

addSbtPlugin("com.danieltrinh" % "sbt-scalariform" % "1.3.0-SNAPSHOT")

// ===============

