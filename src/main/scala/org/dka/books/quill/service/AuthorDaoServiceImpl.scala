package org.dka.books.quill.service

import io.getquill.*
import io.getquill.autoQuote
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres
import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.model.item.Author
import org.dka.books.domain.services.{DaoException, DeleteException, InsertException, QueryException}
import org.dka.books.quill.service.tables.AuthorTable.*
import org.dka.books.quill.service.tables.{AuthorTable, Tables}
import zio.ZIO.fail
import zio.{ZIO, ZLayer, *}


import java.sql.SQLException

/**
 * implementation of AuthorDaoService using quill for queries
 */
final case class AuthorDaoServiceImpl(
  quill: QuillContext,
  tables: Tables)
  extends AuthorDaoService,
    CrudSupport[Author] {

  import quill.*
  import tables.*

  //
  // crud operations
  // todo: refactor into CrudSupport...
  //

  override def create(author: Author): ZIO[Any, DaoException, Author] =
    quill
      .run(
        authors.insertValue(lift(fromDomain(author)))
      )
      .catchAll(ex => catchInsert(ex, author))
      .as(author)

  override def read(id: ID): ZIO[Any, DaoException, Option[Author]] =
    quill
      .run(
        for {
          author <- authors if author.id == lift(id.value.toString)
        } yield author
      )
      .map(_.headOption.map(toDomain))
      .catchAll(ex => catchRead(ex, id))

  override def delete(id: ID): ZIO[Any, DaoException, ID] =
    quill
      .run(
        authors.filter(a => a.id == lift(id.value.toString)).delete
      )
      .catchAll(ex => catchDelete(ex, id))
      .as(id)

  //
  // speciality methods
  //
  inline override def getAll: ZIO[Any, DaoException, List[Author]] =
//    getAllOfThem.map(_.map(toDomain)).catchAll(ex => catchQueryList("could not get all authors", ex))
        quill
        .run(authors)
        .map(_.map(toDomain))
        .catchAll(ex => catchQueryList("could not get all authors", ex))


  inline def getAllOfThem = quill.run(authors)


  override def getByLastName(lastName: String): ZIO[Any, DaoException, List[Author]] =
    quill
      .run(authors.filter(a => a.lastName == quill.lift(lastName)))
      .map(_.map(toDomain))
      .catchAll(ex => catchQueryList(s"could not get by lastName: $lastName", ex))



}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object AuthorDaoServiceImpl {

  val layer: ZLayer[QuillContext with Tables, Nothing, AuthorDaoServiceImpl] =
    ZLayer.fromFunction(AuthorDaoServiceImpl.apply _)

}
