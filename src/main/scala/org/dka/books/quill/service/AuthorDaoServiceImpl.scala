package org.dka.books.quill.service

import java.sql.SQLException

import zio.*
import zio.{ZIO, ZLayer}
import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres

import org.dka.books.domain.model.item.Author
import org.dka.books.quill.service.tables.AuthorTable.*
import org.dka.books.quill.service.tables.Tables

/**
 * implementation of AuthorDaoService using quill for queries
 */
final case class AuthorDaoServiceImpl (quill: QuillContext, tables: Tables) extends AuthorDaoService {
  import quill._ // decoders, etc
  import tables._


  override def getAll: ZIO[Any, SQLException, List[Author]] = {
    quill.run(authors)
      .map(_.map(toDomain))
  }

  override def getByLastName(lastName: String): ZIO[Any, SQLException, List[Author]] = {
    quill.run(authors.filter(a => a.lastName == quill.lift(lastName)))
      .map(_.map(toDomain))
  }
}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object AuthorDaoServiceImpl {
  val layer = ZLayer.fromFunction(AuthorDaoServiceImpl.apply _)
}
