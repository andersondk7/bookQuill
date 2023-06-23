package org.dka.books.quill.service

import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.services.DaoException
import zio.ZIO

/**
 * Generic Crud operations
 * @tparam D
 *   type of domain object
 */
trait CrudService[D] {

  def create(item: D): ZIO[Any, DaoException, D]

  def read(id: ID): ZIO[Any, DaoException, Option[D]]

  def delete(id: ID): ZIO[Any, DaoException, ID]
  // todo: Update ...

}
