package org.dka.books.quill.service

import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres
import zio.ZIO.fail
import zio.{ZIO, ZLayer, *}
import org.dka.books.domain.model.fields.{ID, Title}
import org.dka.books.domain.model.item.Book
import org.dka.books.domain.model.query.BookAuthorSummary
import org.dka.books.domain.services.{DaoException, DeleteException, InsertException, QueryException}
import org.dka.books.quill.service.tables.BookTable.*
import org.dka.books.quill.service.tables.{AuthorTable, BookTable, Tables}

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

  override def getByTitle(title: Title): ZIO[Any, DaoException, List[Book]] = {
    val query = quote {
      books.filter(b => b.title == lift(title.value))
    }
    ctx
      .run(query)
      .map(_.map(toDomain))
      .catchAll(ex => catchQueryList(s"could not find $title", ex))
  }

  override def getBookAuthorSummary(bookId: ID): ZIO[Any, DaoException, List[BookAuthorSummary]] =
    ctx
      .run(
        for {
          bas <- authorsBooks if bas.bookId == lift(bookId.value.toString)
          b   <- books.join(_.id == bas.bookId)
          a   <- authors.join(_.id == bas.authorId)
        } yield (b.title, a.lastName, a.firstName, bas.authorOrder)
      )
      .map(_.map(BookDaoServiceImpl.BasToDomain))
      .catchAll(ex =>
        ZIO.fail(
          QueryException(
            s"could not find authors for book: $bookId",
            Some(DaoException.fromSQL(ex))
          )
        ))

  override def getAuthorBookSummary(authorId: ID): ZIO[Any, DaoException, List[BookAuthorSummary]] =
    ctx
      .run(
        for {
          bas <- authorsBooks if bas.authorId == lift(authorId.value.toString)
          b   <- books.join(_.id == bas.bookId)
          a   <- authors.join(_.id == bas.authorId)
        } yield (b.title, a.lastName, a.firstName, bas.authorOrder)
      )
      .map(_.map(BookDaoServiceImpl.BasToDomain))
      .catchAll(ex =>
        ZIO.fail(
          QueryException(
            s"could not find books for author: $authorId",
            Some(DaoException.fromSQL(ex))
          )
        ))

}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object BookDaoServiceImpl {

  private def BasToDomain(tuple: (String, String, Option[String], Int)): BookAuthorSummary = {
    val (title, lastName, firstName, authorOrder) = tuple
    BookAuthorSummary.fromDB(title, lastName, firstName, authorOrder)
  }

  val layer: ZLayer[QuillContext with Tables, Nothing, BookDaoServiceImpl] =
    ZLayer.fromFunction(BookDaoServiceImpl.apply _)

}
