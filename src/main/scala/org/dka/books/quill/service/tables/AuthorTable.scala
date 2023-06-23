package org.dka.books.quill.service.tables

import java.sql.Timestamp

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*

/*
if I understood zio encoder/decoders better then I could may be able to
skip this layer and just define the Tables Quoted[EntityQuery[DomainItem] directly
 */

/**
 * represents the db table this is NOT a domain object but a raw database object. The domain layer is responsible for:
 * wire (json, etc.) translation data validation data types (rather than primitive string, int, float, etc.) This layer
 * is only responsible for
 *   - read/write to the underlying database tables
 *   - conversion to/from domain types
 */
final case class AuthorTable(
  override val id: String,
  override val version: Int,
  lastName: String,
  firstName: Option[String],
  locationId: Option[String],
  override val createDate: Timestamp,
  override val updateDate: Option[Timestamp])
  extends TableUpdate

object AuthorTable extends DomainSupport[AuthorTable, Author] {

  override def toDomain(db: AuthorTable): Author = Author(
    id = ID.build(db.id),
    version = Version.build(db.version),
    lastName = LastName.build(db.lastName),
    firstName = FirstName.build(db.firstName),
    locationId = LocationID.fromOpt(db.locationId),
    createDate = CreateDate.build(db.createDate),
    lastUpdate = db.updateDate.map(UpdateDate.build)
  )

  override def fromDomain(author: Author): AuthorTable = AuthorTable(
    id = author.id.value.toString,
    version = author.version.value,
    lastName = author.lastName.value,
    firstName = author.firstName.map(_.value),
    locationId = author.locationId.map(_.value.toString),
    createDate = author.createDate.asTimestamp,
    updateDate = author.lastUpdate.map(_.asTimeStamp)
  )

}
