package org.dka.books.quill.service

import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres

import zio.ZIO.fail
import zio.{ZIO, ZLayer, *}

import org.dka.books.domain.model.fields.{ID, Title}
import org.dka.books.domain.model.item.Country
import org.dka.books.domain.services.{DaoException, DeleteException, InsertException, QueryException}
import org.dka.books.quill.service.tables.CountryTable.*
import org.dka.books.quill.service.tables.{CountryTable, Tables}

import java.sql.SQLException

final case class CountryDaoServiceImpl(ctx: QuillContext, tables: Tables)
  extends CountryDaoService,
    CrudSupport[Country] {

  import ctx.*
  import tables.*

  //
  // crud operations
  // todo: refactor into CrudSupport...
  //

  override def create(country: Country): ZIO[Any, DaoException, Country] = ctx
    .run(
      countries.insertValue(lift(fromDomain(country)))
    )
    .catchAll(ex => catchInsert(ex, country))
    .as(country)

  override def read(id: ID): ZIO[Any, DaoException, Option[Country]] = ctx
    .run(
      for {
        country <- countries if country.id == lift(id.value.toString)
      } yield country
    )
    .map(_.headOption.map(toDomain))
    .catchAll(ex => catchRead(ex, id))

  override def delete(id: ID): ZIO[Any, DaoException, ID] =
    ctx
      .run(
        countries.filter(c => c.id == lift(id.value.toString)).delete
      )
      .catchAll(ex => catchDelete(ex, id))
      .as(id)
  //
  // speciality operations
  //

}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object CountryDaoServiceImpl {

  val layer: ZLayer[QuillContext with Tables, Nothing, CountryDaoServiceImpl] =
    ZLayer.fromFunction(CountryDaoServiceImpl.apply _)

}
