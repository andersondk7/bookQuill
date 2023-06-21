package org.dka.books.quill.service

import java.sql.SQLException
import zio.ZIO
import org.dka.books.domain.model.item.Author

/**
 * define access for Authors
 */
trait AuthorDaoService {
  def getByLastName(lastName: String): ZIO[Any, SQLException, List[Author]]
  def getAll: ZIO[Any, SQLException, List[Author]]
}

/**
 * extract specific implementation from environment and execute methods
 */
object AuthorDaoService {
  def getAuthors = ZIO.serviceWithZIO[AuthorDaoService](_.getAll)
  def getAuthors(lastName: String) = ZIO.serviceWithZIO[AuthorDaoService](_.getByLastName(lastName))
}


