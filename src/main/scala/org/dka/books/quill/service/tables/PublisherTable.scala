package org.dka.books.quill.service.tables

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*

import java.sql.Timestamp

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
final case class PublisherTable(
  override val id: String,
  override val version: Int,
  publisherName: String,
  locationId: Option[String],
  website: Option[String],
  override val createDate: Timestamp,
  override val updateDate: Option[Timestamp])
  extends TableUpdate

object PublisherTable extends DomainSupport[PublisherTable, Publisher] {

  override def toDomain(db: PublisherTable): Publisher = Publisher(
    id = ID.build(db.id),
    version = Version.build(db.version),
    publisherName = PublisherName.build(db.publisherName),
    locationId = LocationID.fromOpt(db.locationId),
    webSite = WebSite.fromOpt(db.website),
    createDate = CreateDate.build(db.createDate),
    lastUpdate = db.updateDate.map(UpdateDate.build)
  )

  override def fromDomain(publisher: Publisher): PublisherTable = PublisherTable(
    id = publisher.id.value.toString,
    version = publisher.version.value,
    publisherName = publisher.publisherName.value,
    locationId = publisher.locationId.map(_.value.toString),
    website = publisher.webSite.map(_.value),
    createDate = publisher.createDate.asTimestamp,
    updateDate = publisher.lastUpdate.map(_.asTimeStamp)
  )

}
