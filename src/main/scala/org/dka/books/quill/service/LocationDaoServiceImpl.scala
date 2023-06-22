package org.dka.books.quill.service

import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres
import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.model.item.Location
import org.dka.books.domain.services.{DaoException, DeleteException, InsertException, QueryException}
import org.dka.books.quill.service.tables.LocationTable.*
import org.dka.books.quill.service.tables.{LocationTable, Tables}
import zio.ZIO.fail
import zio.{ZIO, ZLayer, *}

import java.sql.SQLException

/**
 * implementation of LocationDaoService using quill for queries
 */
final case class LocationDaoServiceImpl(
  quill: QuillContext,
  tables: Tables)
  extends LocationDaoService,
    CrudSupport[Location] {

  import quill.*
  import tables.*

  //
  // crud operations
  // todo: refactor into CrudSupport...
  //

  override def create(location: Location): ZIO[Any, DaoException, Location] =
    quill
      .run(
        locations.insertValue(lift(fromDomain(location)))
      )
      .catchAll(ex => catchInsert(ex, location))
      .as(location)

  override def read(id: ID): ZIO[Any, DaoException, Option[Location]] =
    quill
      .run(
        for {
          location <- locations if location.id == lift(id.value.toString)
        } yield location
      )
      .map(_.headOption.map(toDomain))
      .catchAll(ex => catchRead(ex, id))

  override def delete(id: ID): ZIO[Any, DaoException, ID] =
    quill
      .run(
        locations.filter(l => l.id == lift(id.value.toString)).delete
      )
      .catchAll(ex => catchDelete(ex, id))
      .as(id)

  //
  // speciality methods
  //

}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object LocationDaoServiceImpl {

  val layer: ZLayer[QuillContext with Tables, Nothing, LocationDaoServiceImpl] =
    ZLayer.fromFunction(LocationDaoServiceImpl.apply _)

}
