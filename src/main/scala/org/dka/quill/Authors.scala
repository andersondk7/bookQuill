package org.dka.quill

import io.getquill.*

import javax.sql.DataSource
import io.getquill.jdbczio.Quill
import zio._
import zio.Console.printLine

import java.sql.SQLException

final case class Author(
                         id: String,
                         version: Int,
                         lastName: String,
                         firstName: Option[String],
                         locationId: Option[String]
                       )

/**
 * define access for Authors
 */
trait AuthorDaoService {
  def getByLastName(lastName: String): ZIO[Any, SQLException, List[Author]]
  def getAll: ZIO[Any, SQLException, List[Author]]
}

/**
 * extract implementation from environment and execute methods
 */
object AuthorDaoService {
  def getAuthors = ZIO.serviceWithZIO[AuthorDaoService](_.getAll)
  def getAuthors(lastName: String) = ZIO.serviceWithZIO[AuthorDaoService](_.getByLastName(lastName))
}

/**
 * implementation of AuthorDaoService using quill for queries
 */
final case class AuthorDaoServiceImpl (quill: Quill.Postgres[SnakeCase]) extends AuthorDaoService {
  import quill.*

  override def getAll: ZIO[Any, SQLException, List[Author]] = run(query[Author])
  override def getByLastName(lastName: String): ZIO[Any, SQLException, List[Author]] = run(query[Author].filter(a => a.lastName == lift(lastName)))
}

/**
 * companion object provdies constructors
 */
object AuthorDaoServiceImpl {
  val layer = ZLayer.fromFunction(AuthorDaoServiceImpl.apply _)
}

object Authors extends ZIOAppDefault {

  val dataSourceLayer: ZLayer[Any, Throwable, DataSource] =
    Quill.DataSource.fromPrefix("postgres")
  val quillLayer: ZLayer[DataSource, Nothing, Quill.Postgres[SnakeCase]] =
    Quill.Postgres.fromNamingStrategy(SnakeCase)

//  override def run = {
//    AuthorDaoService.getAuthors
//      .provide(
//        quillLayer,
//        dataSourceLayer,
//        AuthorDaoServiceImpl.layer
//      ).debug("Results")
//      .exitCode
//  }

  // lifted from the documenation's IdiomaticApp
  // https://github.com/zio/zio-quill/blob/master/quill-jdbc-zio/src/test/scala/io/getquill/examples/IdiomaticApp.scala

  override def run =
    (for {
      adams <- AuthorDaoService.getAuthors("Adam")
      _ <- printLine(adams)
      allAuthors <- AuthorDaoService.getAuthors
      _ <- printLine(allAuthors)
    } yield ())
      .provide(
        AuthorDaoServiceImpl.layer,
        quillLayer,
        dataSourceLayer
      )
}