object DependencyGraphSettings {

  import net.virtualvoid.sbt.graph.Plugin._

  def apply() = graphSettings ++
                additionalSettings

  protected def additionalSettings = Seq(
    filterScalaLibrary := false
  )
}