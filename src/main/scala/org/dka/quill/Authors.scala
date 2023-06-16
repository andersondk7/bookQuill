package org.dka.quill

import io.getquill._

import javax.sql.DataSource
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres
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
 * extract specific implementation from environment and execute methods
 */
object AuthorDaoService {
  def getAuthors = ZIO.serviceWithZIO[AuthorDaoService](_.getAll)
  def getAuthors(lastName: String) = ZIO.serviceWithZIO[AuthorDaoService](_.getByLastName(lastName))
}

/**
 * allows for mapping of case class types to table names
 */
final case class Tables(quill: QuillContext) {
  import quill._

  val authors = quote { querySchema[Author]("authors")}
}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object Tables {
  val authorTableLayer = ZLayer.fromFunction(Author.apply _)
  val tablesLayer = ZLayer.fromFunction(Tables.apply _)
}

/**
 * making the type of datasource explicit for Postgres
 * @param dss
 */
final case class QuillContext(dss: DataSource) extends Postgres(SnakeCase, dss)

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object QuillContext {
  val quillLayer = ZLayer.fromFunction(QuillContext.apply _)
  val dataSourceLayer = Quill.DataSource.fromPrefix("postgres")
}

/**
 * implementation of AuthorDaoService using quill for queries
 */
final case class AuthorDaoServiceImpl (quill: QuillContext, tables: Tables) extends AuthorDaoService {
  import quill._
  import tables._


  override def getAll: ZIO[Any, SQLException, List[Author]] = run(authors)
  override def getByLastName(lastName: String): ZIO[Any, SQLException, List[Author]] = run(authors.filter(a => a.lastName == lift(lastName)))
}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object AuthorDaoServiceImpl {
  val layer = ZLayer.fromFunction(AuthorDaoServiceImpl.apply _)
}
