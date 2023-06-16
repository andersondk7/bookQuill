import Dependencies.*

lazy val scala3 = "3.3.0"

ThisBuild / organization := "org.dka.quill"
ThisBuild / version := "0.1.1-SNAPSHOT"
ThisBuild / scalaVersion := scala3


lazy val quill = project.in(file("."))
  .configs(IntegrationTest)
  .settings(
    libraryDependencies ++= Seq(
      commonDependencies,
      zioDependencies
    ).flatten,
    Defaults.itSettings
  )
