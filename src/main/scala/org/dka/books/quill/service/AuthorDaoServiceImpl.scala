package org.dka.books.quill.service

import zio.*
import zio.{ZIO, ZLayer}
import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres

import java.sql.SQLException
import org.dka.books.domain.model.item.Author
import org.dka.books.quill.domain.DBAuthor
import org.dka.books.quill.domain.DBAuthor.toDomain

/**
 * implementation of AuthorDaoService using quill for queries
 */
final case class AuthorDaoServiceImpl (quill: QuillContext, tables: Tables) extends AuthorDaoService {
  import quill._ // decoders, etc
  import quill.stringDecoder
  import tables._


  override def getAll: ZIO[Any, SQLException, List[DBAuthor]] = {
    quill.run(authors)
  }

  override def getByLastName(lastName: String): ZIO[Any, SQLException, List[Author]] = {
    val result = quill.run(authors.filter(a => a.lastName == quill.lift(lastName)))
      .map(_.map(DBAuthor.toDomain))
    result
  }
}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object AuthorDaoServiceImpl {
  val layer = ZLayer.fromFunction(AuthorDaoServiceImpl.apply _)
}
