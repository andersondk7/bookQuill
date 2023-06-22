package org.dka.books.quill.service

import java.sql.SQLException
import zio.ZIO

import org.dka.books.domain.model.item.Author
import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.services.DaoException

/**
 * additional services for authors beyond those defined in the CrudService
 */
trait AuthorDaoService extends CrudService[Author] {
  
  
  def getByLastName(lastName: String): ZIO[Any, DaoException, List[Author]]
  def getAll: ZIO[Any, DaoException, List[Author]]
}

/**
 * extract specific implementation from environment and execute methods
 */
object AuthorDaoService {
  
  //
  // crud operations
  //
  def get(id: ID) = ZIO.serviceWithZIO[AuthorDaoService](_.read(id))
  def create(author: Author) = ZIO.serviceWithZIO[AuthorDaoService](_.create(author))
  def delete(id: ID) = ZIO.serviceWithZIO[AuthorDaoService](_.delete(id))

  //
  // speciality operations
  //
  def getAll = ZIO.serviceWithZIO[AuthorDaoService](_.getAll)
  def getByLastName(lastName: String) = ZIO.serviceWithZIO[AuthorDaoService](_.getByLastName(lastName))
}


