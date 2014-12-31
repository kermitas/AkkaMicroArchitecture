import sbt._
import sbt.Keys._

object ScalaSettings {
  def apply() = Seq(
    scalaVersion                    := "2.11.4",
    crossScalaVersions              := Seq("2.10.4", "2.11.4"),
    scalacOptions                   ++= Seq("-feature", "-unchecked", "-deprecation"),
    incOptions                      := incOptions.value.withNameHashing(true), // incremental compilation (by Grzegorz Kossakowski), announced https://typesafe.com/blog/scala-211-has-arrived
    scalacOptions in (Compile, doc) ++= Seq("-diagrams", "-implicits") // generates diagrams in documentation, I am not sure if you should have
                                                                       // installed Graphviz http://www.graphviz.org/ (the "dot" command in your shell)
  )
}