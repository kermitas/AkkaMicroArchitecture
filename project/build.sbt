// This is configuration for SBT itself.
// We are interested if SBT will produce any warnings during compilation of our sbt files (for example: when we will use too old plugin).

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")