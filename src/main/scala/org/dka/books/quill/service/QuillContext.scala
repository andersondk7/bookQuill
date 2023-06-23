package org.dka.books.quill.service

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import io.getquill.jdbczio.Quill.Postgres
import zio.ZLayer

import javax.sql.DataSource

/**
 * making the type of datasource explicit for Postgres
 */
final case class QuillContext(dataSource: DataSource) extends Postgres(SnakeCase, dataSource)

/**
 * following the companion object --> construction --> ZIO Layer model
 */
object QuillContext {

  val quillLayer: ZLayer[DataSource, Nothing, QuillContext] = ZLayer.fromFunction(QuillContext.apply _)

  val dataSourceLayer: ZLayer[Any, Throwable, DataSource] = Quill.DataSource.fromPrefix("postgres")

}
