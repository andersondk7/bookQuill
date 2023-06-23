package org.dka.books.quill.service.tables

import java.sql.Timestamp

import io.getquill.*
import zio.ZLayer

import org.dka.books.quill.service.QuillContext
import org.dka.books.quill.service.tables.*

/**
 * allows for mapping of table types (i.e. case class) to table names
 */
final case class Tables(quill: QuillContext) {

  import Tables.*
  import quill.*

  val authors: Quoted[EntityQuery[AuthorTable]] = quote(querySchema[AuthorTable]("authors"))

  val books: Quoted[EntityQuery[BookTable]] = quote(querySchema[BookTable]("books"))

  val countries: Quoted[EntityQuery[CountryTable]] = quote(querySchema[CountryTable]("countries"))

  val locations: Quoted[EntityQuery[LocationTable]] = quote(querySchema[LocationTable]("locations"))

  val publishers: Quoted[EntityQuery[PublisherTable]] = quote(querySchema[PublisherTable]("publishers"))

  // todo: change to authorBookSummary, in both db, domain, anorm, and quill
  val authorsBooks: Quoted[EntityQuery[AuthorBook]] = quote(querySchema[AuthorBook]("authors_books"))

}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object Tables {

  val tablesLayer: ZLayer[QuillContext, Nothing, Tables] = ZLayer.fromFunction(Tables.apply _)

}
