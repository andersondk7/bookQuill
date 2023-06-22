package org.dka.books.quill.service

import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres

import zio.ZIO.fail
import zio.{ZIO, ZLayer, *}

import org.dka.books.domain.model.fields.{ID, Title}
import org.dka.books.domain.model.item.Book
import org.dka.books.domain.services.{DaoException, DeleteException, InsertException, QueryException}
import org.dka.books.quill.service.tables.BookTable.*
import org.dka.books.quill.service.tables.{BookTable, Tables}

import java.sql.SQLException
import scala.tools.nsc.doc.html.HtmlTags

final case class BookDaoServiceImpl(quill: QuillContext, tables: Tables) extends BookDaoService {

  import quill.*
  import tables.*

  //
  // crud operations
  //
  override def create(book: Book): ZIO[Any, DaoException, Book] = {
    val dbItem = fromDomain(book)
    quill
      .run(
        books.insertValue(lift(dbItem))
      )
      .catchAll(ex =>
        ZIO.fail(
          InsertException(
            s"could not insert: $book",
            Some(DaoException.fromSQL(ex))
          )
        ))
      .as(book)
  }

  override def read(id: ID): ZIO[Any, DaoException, Option[Book]] =
    quill
      .run(
        for {
          book <- books if book.id == lift(id.value.toString)
        } yield book
      )
      .map(_.headOption.map(toDomain))
      .catchAll(ex =>
        ZIO.fail(
          QueryException(s"could not read $id", Some(DaoException.fromSQL(ex)))
        ))

  override def delete(id: ID): ZIO[Any, DaoException, ID] =
    quill
      .run(
        books.filter(b => b.id == lift(id.value.toString)).delete
      )
      .catchAll(ex =>
        ZIO.fail(
          DeleteException(s"could not delete $id", Some(DaoException.fromSQL(ex)))
        ))
      .as(id)

  //
  // speciality operations
  //
  override def getAllIds: ZIO[Any, DaoException, List[ID]] =
    quill
      .run(books)
      .map(_.map(b => ID.build(b.id)))
      .catchAll(ex =>
        ZIO.fail(
          QueryException( s"could not get all bookIds", Some(DaoException.fromSQL(ex))
          )
        ))

  override def getByTitle(title: Title): ZIO[Any, DaoException, List[Book]] =
    quill
      .run(books.filter(b => b.title == lift(title.value)))
      .map(_.map(toDomain))
      .catchAll(ex =>
        ZIO.fail(
          QueryException(s"could not get all book with title: $title", Some(DaoException.fromSQL(ex))
          )
        ))
}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object BookDaoServiceImpl {

  val layer: ZLayer[QuillContext with Tables, Nothing, BookDaoServiceImpl] =
    ZLayer.fromFunction(BookDaoServiceImpl.apply _)

}
