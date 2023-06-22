package org.dka.books.quill.service

import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.services.DaoException
import zio.ZIO

/**
 * Generic Crud operations
 * @tparam T type of domain object
 */
trait CrudService[T] {
  def create(item: T): ZIO[Any, DaoException, T]
  def read(id: ID): ZIO[Any, DaoException, Option[T]]
  def delete(id: ID): ZIO[Any, DaoException, ID]
  // todo: Update ...
}
