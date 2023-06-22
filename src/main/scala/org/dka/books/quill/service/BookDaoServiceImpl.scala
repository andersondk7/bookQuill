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

final case class BookDaoServiceImpl(ctx: QuillContext, tables: Tables) extends BookDaoService, CrudSupport[Book] {

  import ctx.*
  import tables.*

  //
  // crud operations
  // todo: refactor into CrudSupport...
  //
  override def create(book: Book): ZIO[Any, DaoException, Book] =
    ctx
      .run(
        books.insertValue(lift(fromDomain(book)))
      )
      .catchAll(ex => catchInsert(ex, book))
      .as(book)

  override def read(id: ID): ZIO[Any, DaoException, Option[Book]] =
    ctx
      .run(
        for {
          book <- books if book.id == lift(id.value.toString)
        } yield book
      )
      .map(_.headOption.map(toDomain))
      .catchAll(ex => catchRead(ex, id))

  override def delete(id: ID): ZIO[Any, DaoException, ID] =
    ctx
      .run(
        books.filter(b => b.id == lift(id.value.toString)).delete
      )
      .catchAll(ex => catchDelete(ex, id))
      .as(id)

  //
  // speciality operations
  //
  override def getAllIds: ZIO[Any, DaoException, List[ID]] =
    ctx
      .run(books)
      .map(_.map(b => ID.build(b.id)))
      .catchAll(ex => catchQueryIds("could not get all bookIds", ex))

  override def getByTitle(title: Title): ZIO[Any, DaoException, List[Book]] =
    ctx
      .run(books.filter(b => b.title == lift(title.value)))
      .map(_.map(toDomain))
      .catchAll(ex => catchQueryList(s"could not find $title", ex))

}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object BookDaoServiceImpl {

  val layer: ZLayer[QuillContext with Tables, Nothing, BookDaoServiceImpl] =
    ZLayer.fromFunction(BookDaoServiceImpl.apply _)

}
