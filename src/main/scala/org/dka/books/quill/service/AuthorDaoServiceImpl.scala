package org.dka.books.quill.service

import io.getquill.*
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
final case class AuthorDaoServiceImpl(quill: QuillContext, tables: Tables) extends AuthorDaoService {

  import quill.*
  import tables.*

  //
  // crud operations
  //

  override def create(author: Author): ZIO[Any, DaoException, Author] = {
    val dbItem = fromDomain(author)
    quill
      .run(
        authors.insertValue(lift(dbItem))
      )
      .catchAll(ex =>
        ZIO.fail(
          InsertException(
            s"could not insert: $author",
            Some(DaoException.fromSQL(ex))
          )
        ))
      .as(author)
  }

  override def read(id: ID): ZIO[Any, DaoException, Option[Author]] =
    quill
      .run(
        for {
          author <- authors if author.id == lift(id.value.toString)
        } yield author
      )
      .map(_.headOption.map(toDomain))
      .catchAll(ex =>
        ZIO.fail(
          QueryException(s"could not read $id", Some(DaoException.fromSQL(ex)))
        ))

  override def delete(id: ID): ZIO[Any, DaoException, ID] =
    quill
      .run(
        authors.filter(a => a.id == lift(id.value.toString)).delete
      )
      .catchAll(ex =>
        ZIO.fail(
          DeleteException(s"could not delete $id", Some(DaoException.fromSQL(ex)))
        ))
      .as(id)

  //
  // speciality methods
  //
  override def getAll: ZIO[Any, DaoException, List[Author]] =
    quill
      .run(authors)
      .map(_.map(toDomain))
      .catchAll(ex =>
        ZIO.fail(
          QueryException(s"could not get all authors", Some(DaoException.fromSQL(ex)))
        ))

  override def getByLastName(lastName: String): ZIO[Any, DaoException, List[Author]] =
    quill
      .run(authors.filter(a => a.lastName == quill.lift(lastName)))
      .map(_.map(toDomain))
      .catchAll(ex => ZIO.fail(DaoException.fromSQL(ex)))

}

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object AuthorDaoServiceImpl {

  val layer: ZLayer[QuillContext with Tables, Nothing, AuthorDaoServiceImpl] =
    ZLayer.fromFunction(AuthorDaoServiceImpl.apply _)

}
