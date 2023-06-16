package org.dka.quill

// lifted from documentation
// see https://zio.dev/zio-quill/getting-started

import io.getquill._
import io.getquill.jdbczio.Quill
import zio._

import java.sql.SQLException

case class Pet(name: String, age: Int)

class DataService(quill: Quill.Postgres[SnakeCase]) {
  import quill._
  def getPeople: ZIO[Any, SQLException, List[Pet]] = run(query[Pet])
}
object DataService {
  def getPeople: ZIO[DataService, SQLException, List[Pet]] =
    ZIO.serviceWithZIO[DataService](_.getPeople)

  val live = ZLayer.fromFunction(new DataService(_))
}
object DocumentationMain extends ZIOAppDefault {
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