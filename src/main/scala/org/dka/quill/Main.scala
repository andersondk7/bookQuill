package org.dka.quill

// lifted from documentation
// https://zio.dev/zio-quill/getting-started
// see https://github.com/deusaquilus/zio-quill-gettingstarted/blob/master/src/main/scala/example/module/Main.scala

import io.getquill._
import io.getquill.jdbczio.Quill
import zio._

import java.sql.SQLException

final case class Person(name: String, age: Int)

trait PeopleDataService {
  def getPeople: ZIO[Any, SQLException, List[Person]]
  def getPeopleOlderThan(age: Int): ZIO[Any, SQLException, List[Person]]
}

object PeopleDataService {
  def getPeople = ZIO.serviceWithZIO[PeopleDataService](_.getPeople)
  def getPeopleOlderThan(age: Int) = ZIO.serviceWithZIO[PeopleDataService](_.getPeopleOlderThan(age))
}

object DataServiceLive {
  val layer = ZLayer.fromFunction(DataServiceLive.apply _)
}

final case class DataServiceLive(quill: Quill.Postgres[SnakeCase]) extends PeopleDataService {
  import quill._
  def getPeople =
    run(query[Person])
  def getPeopleOlderThan(age: Int) =
    run(query[Person].filter(p => p.age > lift(age)))
}

/**
 * Demonstrates using Quill with a ZIO Module 2.0 pattern.
 */
object MainIdiom extends ZIOAppDefault {

  val quillLayer = Quill.Postgres.fromNamingStrategy(SnakeCase)
  val dsLayer = Quill.DataSource.fromPrefix("postgres")

  override def run =
    PeopleDataService.getPeople
      .provide(quillLayer, dsLayer, DataServiceLive.layer)
      .debug("Results")
      .exitCode
}