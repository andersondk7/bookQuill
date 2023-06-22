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

}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object Tables {

  val tablesLayer = ZLayer.fromFunction(Tables.apply _)

}
