package org.dka.books.quill.service

import java.sql.SQLException
import zio.ZIO
import org.dka.books.domain.model.item.Country
import org.dka.books.domain.model.fields.{CountryAbbreviation, CountryName, ID}
import org.dka.books.domain.services.DaoException

trait CountryDaoService extends CrudService[Country] {

  // no specialty queries
}

object CountryDaoService {

  //
  // crud operations
  //
  def get(id: ID) = ZIO.serviceWithZIO[CountryDaoService](_.read(id))

  def create(country: Country) = ZIO.serviceWithZIO[CountryDaoService](_.create(country))

  def delete(id: ID) = ZIO.serviceWithZIO[CountryDaoService](_.delete(id))

}
