import Dependencies.*

lazy val scala3 = "3.3.0"
lazy val scala2 = "2.13.11"

ThisBuild / organization := "org.dka.quill"
ThisBuild / version := "0.1.1-SNAPSHOT"
ThisBuild / scalaVersion := scala3
//ThisBuild / scalaVersion := scala2


lazy val quill = project.in(file("."))
  .configs(IntegrationTest)
  .settings(
    libraryDependencies ++= Seq(
      commonDependencies,
      zioDependencies
    ).flatten,
    Defaults.itSettings
  )
