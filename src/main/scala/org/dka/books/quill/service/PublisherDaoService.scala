package org.dka.books.quill.service

import java.sql.SQLException
import zio.ZIO

import org.dka.books.domain.model.item.Publisher
import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.services.DaoException

/**
 * additional services for authors beyond those defined in the CrudService
 */
trait PublisherDaoService extends CrudService[Publisher] {}

/**
 * extract specific implementation from environment and execute methods
 */
object PublisherDaoService {

  //
  // crud operations
  //
  def get(id: ID) = ZIO.serviceWithZIO[PublisherDaoService](_.read(id))

  def create(publisher: Publisher) = ZIO.serviceWithZIO[PublisherDaoService](_.create(publisher))

  def delete(id: ID) = ZIO.serviceWithZIO[PublisherDaoService](_.delete(id))

  //
  // speciality operations
  //

}
