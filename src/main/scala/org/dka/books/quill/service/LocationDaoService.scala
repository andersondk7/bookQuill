package org.dka.books.quill.service

import java.sql.SQLException
import zio.ZIO

import org.dka.books.domain.model.item.Location
import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.services.DaoException

/**
 * additional services for authors beyond those defined in the CrudService
 */
trait LocationDaoService extends CrudService[Location] {}

/**
 * extract specific implementation from environment and execute methods
 */
object LocationDaoService {

  //
  // crud operations
  //
  def get(id: ID) = ZIO.serviceWithZIO[LocationDaoService](_.read(id))

  def create(location: Location) = ZIO.serviceWithZIO[LocationDaoService](_.create(location))

  def delete(id: ID) = ZIO.serviceWithZIO[LocationDaoService](_.delete(id))

  //
  // speciality operations
  //

}
