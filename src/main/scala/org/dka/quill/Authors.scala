package org.dka.quill

import io.getquill._
import io.getquill.jdbczio.Quill
import zio.Console.printLine
import zio._

import java.sql.SQLException

final case class Author(
                         id: String,
                         version: Int,
                         lastName: String,
                         firstName: Option[String],
                         locationId: Option[String]
                       )

object Authors extends ZIOAppDefault {

  case class DataService(quill: Quill.Postgres[Literal]) {
    import quill._
    val authors       = quote(query[Author])
    def authorsByLastName = quote((lastName: String) => authors.filter(p => p.lastName == lastName))
  }
  case class ApplicationLive(dataService: DataService) {
    import dataService.quill._
    import dataService.quill
    def getAuthorsByLastName(lastName: String): ZIO[Any, SQLException, List[Author]] =
      quill.run(dataService.authorsByLastName(lift(lastName)))
    def getAllAuthors(): ZIO[Any, SQLException, List[Author]] = quill.run(dataService.authors)
  }
  object Application {
    def getAuthorsByLastname(name: String) =
      ZIO.serviceWithZIO[ApplicationLive](_.getAuthorsByLastName(name))
    def getAllAuthors() =
      ZIO.serviceWithZIO[ApplicationLive](_.getAllAuthors())
  }

  val dataServiceLive = ZLayer.fromFunction(DataService.apply _)
  val applicationLive = ZLayer.fromFunction(ApplicationLive.apply _)
  val dataSourceLive  = Quill.DataSource.fromPrefix("postgres")
  val postgresLive    = Quill.Postgres.fromNamingStrategy(Literal)

  override def run =
    (for {
      adams      <- Application.getAuthorsByLastname("Adams")
      _         <- printLine(adams)
      allAuthors <- Application.getAllAuthors()
      _         <- printLine(allAuthors)
    } yield ()).provide(applicationLive, dataServiceLive, dataSourceLive, postgresLive)
}