import sbt.Keys._
import sbt._
import sbtassembly.Plugin._
import AssemblyKeys._

/**
 * sbt-assembly settings.
 */
object AssemblySettings {
  def apply(mainClass: String) = assemblySettings ++
                                 additionalSettings(mainClass)

  protected def additionalSettings(mainClassFullyQualifiedName: String) = Seq (
    mainClass in assembly := Some(mainClassFullyQualifiedName)
  )
}
