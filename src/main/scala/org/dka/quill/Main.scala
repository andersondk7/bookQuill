package org.dka.quill

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*

import java.sql.SQLException

case class Person(name: String, age: Int)

class DataService(quill: Quill.Postgres[SnakeCase]) {

  import quill.*

  def getPeople: ZIO[Any, SQLException, List[Person]] = run(query[Person])
}

object DataService {
  def getPeople: ZIO[DataService, SQLException, List[Person]] =
    ZIO.serviceWithZIO[DataService](_.getPeople)

  val live = ZLayer.fromFunction(new DataService(_))
}

object Main extends ZIOAppDefault {
  override def run = {
    DataService.getPeople
      .provide(
        DataService.live,
        Quill.Postgres.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromPrefix("postgres")
      )
      .debug("Results")
      .exitCode
  }
}