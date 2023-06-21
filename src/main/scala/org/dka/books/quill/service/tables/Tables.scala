package org.dka.books.quill.service.tables

import java.sql.Timestamp

import io.getquill.*
import zio.ZLayer

import org.dka.books.quill.service.QuillContext
import org.dka.books.quill.service.tables.*

/**
 * allows for mapping of case class types to table names
 */
final case class Tables(quill: QuillContext) {
  import Tables.*
  import quill.*

  val authors: Quoted[EntityQuery[AuthorTable]] = quote { querySchema[AuthorTable]("authors")}
}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object Tables {
 
  
  val tablesLayer = ZLayer.fromFunction(Tables.apply _)
}
