package org.dka.books.quill.service.tables

import java.sql.Timestamp
import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*

import java.time.LocalDate

/**
 * represents the db table this is NOT a domain object but a raw database object. The domain layer is responsible for:
 * wire (json, etc.) translation data validation data types (rather than primitive string, int, float, etc.) This layer
 * is only responsible for
 *   - read/write to the underlying database tables
 *   - conversion to/from domain types
 */
final case class BookTable(
  override val id: String,
  override val version: Int,
  title: String,
  price: BigDecimal,
  publisherId: Option[String],
  publishDate: Option[LocalDate],
  override val createDate: Timestamp,
  override val updateDate: Option[Timestamp])
  extends TableUpdate

object BookTable extends DomainSupport[BookTable, Book] {

  override def toDomain(db: BookTable): Book = Book(
    id = ID.build(db.id),
    version = Version.build(db.version),
    title = Title.build(db.title),
    price = Price.build(db.price),
    publisherID = PublisherID.fromOpt(db.publisherId),
    publishDate = PublishDate.build(db.publishDate),
    createDate = CreateDate.build(db.createDate),
    lastUpdate = db.updateDate.map(UpdateDate.build)
  )

  override def fromDomain(book: Book): BookTable = BookTable(
    id = book.id.value.toString,
    version = book.version.value,
    title = book.title.value,
    price = book.price.value,
    publisherId = book.publisherID.map(_.value.toString),
    publishDate = book.publishDate.map(_.value),
    createDate = book.createDate.asTimestamp,
    updateDate = book.lastUpdate.map(_.asTimeStamp)
  )

}
