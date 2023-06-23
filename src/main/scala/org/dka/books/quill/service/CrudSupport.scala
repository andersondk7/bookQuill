package org.dka.books.quill.service

import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres
import org.dka.books.domain.model.fields.ID
import org.dka.books.domain.model.item.Author
import org.dka.books.domain.services.{DaoException, DeleteException, InsertException, QueryException}
import org.dka.books.quill.service.tables.{AuthorTable, Tables}
import zio.ZIO.fail
import zio.{ZIO, ZLayer, *}

import java.sql.SQLException

trait CrudSupport[T] {

  def catchInsert(ex: SQLException, item: T): ZIO[Any, DaoException, T] = ZIO.fail(
    InsertException(s"could not insert: $item", Some(DaoException.fromSQL(ex)))
  )

  def catchRead(ex: SQLException, id: ID): ZIO[Any, DaoException, Option[T]] =
    catchQueryOption(s"could not read $ID", ex)

  def catchQueryOption(text: String, ex: SQLException): ZIO[Any, DaoException, Option[T]] = ZIO.fail(
    QueryException(text, Some(DaoException.fromSQL(ex)))
  )

  def catchQueryList(text: String, ex: SQLException): ZIO[Any, DaoException, List[T]] = ZIO.fail(
    QueryException(text, Some(DaoException.fromSQL(ex)))
  )

  def catchQueryIds(text: String, ex: SQLException): ZIO[Any, DaoException, List[ID]] = ZIO.fail(
    QueryException(text, Some(DaoException.fromSQL(ex)))
  )

  def catchDelete(ex: SQLException, id: ID): ZIO[Any, DaoException, Option[T]] = ZIO.fail(
    DeleteException(s"could not delete $id", Some(DaoException.fromSQL(ex)))
  )

}
