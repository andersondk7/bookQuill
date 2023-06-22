import sbt.*

object Dependencies {

  private val bookapi_version = "0.7.1"
  private val bookgenerator_version = "0.7.1"

  private val cats_version = "2.9.0"
  private val circe_version = "0.14.5"
  private val config_version = "1.4.2"
//  private val hikaricp_version = "2.8.0"
  private val logback_version = "1.4.6"
  private val postgres_driver_version = "42.6.0"
  private val scalalogging_version = "3.9.5"
  private val scalactic_version = "3.2.15"
  private val scalatest_version = "3.2.15"

  private val catsCore = "org.typelevel" %% "cats-core" % cats_version
  private val circeCore = "io.circe" %% "circe-core" % circe_version
  private val circeGeneric = "io.circe" %% "circe-generic" % circe_version
  private val circeParser = "io.circe" %% "circe-parser" % circe_version
  private val logging = "com.typesafe.scala-logging" %% "scala-logging" % scalalogging_version
  private val scalatic = "org.scalactic" %% "scalactic" % scalactic_version
  private val scalaTest = "org.scalatest" %% "scalatest" % scalatest_version % "it,test"
  private val zio_version = "2.0.13"
  private val zio_logging_version = "2.1.13"

  // java libs
//  private val config = "com.typesafe" % "config" % config_version
//  private val hikaricp = "com.zaxxer" % "HikariCP" % hikaricp_version
//  private val hikaricp = "com.zaxxer" % "HikariCP" % "5.0.1"
  private val postgresDriver = "org.postgresql" % "postgresql" % postgres_driver_version
  private val logBack = "ch.qos.logback" % "logback-classic" % logback_version

  // zio libs
  private val quill = "io.getquill" %% "quill-jdbc-zio" % "4.6.0"
  private val zio = "dev.zio" %% "zio" % zio_version
  private val zioLogging = "dev.zio" %% "zio-logging-slf4j-bridge" % zio_logging_version
  private val zioTest = "dev.zio" %% "zio-test" % zio_version % "it, test"
  private val zioTestSbt = "dev.zio" %% "zio-test-sbt" % zio_version % "it, test"
  private val zioTestMagnolia = "dev.zio" %% "zio-test-magnolia" % zio_version % "it, test"

  // book domain libs
  private val bookApi = "org.dka.books" %% "bookdomain" % bookapi_version
  private val bookGenerator = "org.dka.books" %% "bookdb" % bookgenerator_version % "it, test"


  val zioDependencies: Seq[ModuleID] = Seq(
    bookApi,
    bookGenerator,
    quill,
    postgresDriver,
    zio,
    zioLogging,
    zioTest,
    zioTestSbt,
    zioTestMagnolia
  )
}
