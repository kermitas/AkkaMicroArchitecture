object Build extends sbt.Build {

  lazy final val version     = "0.4.8"

  // --- projects definition

  lazy val amaAkka    = AmaAkkaProject(version)
  lazy val amaStartup = AmaStartupProject(version, amaAkka)
  lazy val amaAddons  = AmaAddonsProject(version, amaStartup)
  lazy val amaCore    = AmaCoreProject(version, amaAddons)

  lazy val ama        = AmaProject(version, amaCore)

  lazy val amaSample  = AmaSampleProject(version, ama)
}
