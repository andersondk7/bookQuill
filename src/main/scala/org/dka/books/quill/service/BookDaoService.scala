package org.dka.books.quill.service

import org.dka.books.domain.model.fields.{ID, Title}
import org.dka.books.domain.model.item.Book
import org.dka.books.domain.model.query.BookAuthorSummary
import org.dka.books.domain.services.DaoException
import zio.ZIO

import java.sql.SQLException

trait BookDaoService extends CrudService[Book] {

  def getAllIds: ZIO[Any, DaoException, List[ID]]

  def getByTitle(title: Title): ZIO[Any, DaoException, List[Book]]

  def getBookAuthorSummary(bookId: ID): ZIO[Any, DaoException, List[BookAuthorSummary]]

  def getAuthorBookSummary(authorId: ID): ZIO[Any, DaoException, List[BookAuthorSummary]]

}

object BookDaoService {

  //
  // crud operations
  //
  def get(id: ID): ZIO[BookDaoService, DaoException, Option[Book]] = ZIO.serviceWithZIO[BookDaoService](_.read(id))

  def create(book: Book): ZIO[BookDaoService, DaoException, Book] = ZIO.serviceWithZIO[BookDaoService](_.create(book))

  def delete(id: ID): ZIO[BookDaoService, DaoException, ID] = ZIO.serviceWithZIO[BookDaoService](_.delete(id))

  // speciality operations
  def getIds: ZIO[BookDaoService, DaoException, List[ID]] = ZIO.serviceWithZIO[BookDaoService](_.getAllIds)

  def get(title: Title): ZIO[BookDaoService, DaoException, List[Book]] =
    ZIO.serviceWithZIO[BookDaoService](_.getByTitle(title))

  def getBookAuthorSummary(bookId: ID): ZIO[BookDaoService, DaoException, List[BookAuthorSummary]] =
    ZIO.serviceWithZIO[BookDaoService](_.getBookAuthorSummary(bookId))

  def getAuthorBookSummary(authorId: ID): ZIO[BookDaoService, DaoException, List[BookAuthorSummary]] =
    ZIO.serviceWithZIO[BookDaoService](_.getAuthorBookSummary(authorId))

}
