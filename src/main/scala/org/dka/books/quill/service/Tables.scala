package org.dka.books.quill.service

import zio.ZLayer
import io.getquill._

import org.dka.books.quill.domain.DBAuthor
/**
 * allows for mapping of case class types to table names
 */
final case class Tables(quill: QuillContext) {
  import quill._

  val authors: Quoted[EntityQuery[DBAuthor]] = quote { querySchema[DBAuthor]("authors")}
}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object Tables {
  val authorTableLayer = ZLayer.fromFunction(DBAuthor.apply _)
  val tablesLayer = ZLayer.fromFunction(Tables.apply _)
}
