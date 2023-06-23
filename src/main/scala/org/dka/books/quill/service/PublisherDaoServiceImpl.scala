package org.dka.books.quill.service

import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres
import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.model.item.Publisher
import org.dka.books.domain.services.{DaoException, DeleteException, InsertException, QueryException}
import org.dka.books.quill.service.tables.PublisherTable.*
import org.dka.books.quill.service.tables.{PublisherTable, Tables}
import zio.ZIO.fail
import zio.{ZIO, ZLayer, *}

import java.sql.SQLException

/**
 * implementation of PublisherDaoService using quill for queries
 */
final case class PublisherDaoServiceImpl(
  quill: QuillContext,
  tables: Tables)
  extends PublisherDaoService,
    CrudSupport[Publisher] {

  import quill.*
  import tables.*

  //
  // crud operations
  // todo: refactor into CrudSupport...
  //

  override def create(publisher: Publisher): ZIO[Any, DaoException, Publisher] =
    quill
      .run(
        publishers.insertValue(lift(fromDomain(publisher)))
      )
      .catchAll(ex => catchInsert(ex, publisher))
      .as(publisher)

  override def read(id: ID): ZIO[Any, DaoException, Option[Publisher]] =
    quill
      .run(
        for {
          publisher <- publishers if publisher.id == lift(id.value.toString)
        } yield publisher
      )
      .map(_.headOption.map(toDomain))
      .catchAll(ex => catchRead(ex, id))

  override def delete(id: ID): ZIO[Any, DaoException, ID] =
    quill
      .run(
        publishers.filter(l => l.id == lift(id.value.toString)).delete
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
object PublisherDaoServiceImpl {

  val layer: ZLayer[QuillContext with Tables, Nothing, PublisherDaoServiceImpl] =
    ZLayer.fromFunction(PublisherDaoServiceImpl.apply _)

}
