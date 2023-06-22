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
final case class LocationTable(
  override val id: String,
  override val version: Int,
  locationName: String,
  locationAbbreviation: String,
  countryId: String,
  createDate: Timestamp,
  override val updateDate: Option[Timestamp])
  extends TableUpdate

object LocationTable extends DomainSupport[LocationTable, Location] {

  override def toDomain(db: LocationTable): Location = Location(
    id = ID.build(db.id),
    version = Version.build(db.version),
    locationName = LocationName.build(db.locationName),
    locationAbbreviation = LocationAbbreviation.build(db.locationAbbreviation),
    countryID = CountryID.build(db.countryId),
    createDate = CreateDate.build(db.createDate),
    lastUpdate = db.updateDate.map(UpdateDate.build)
  )

  override def fromDomain(location: Location): LocationTable = LocationTable(
    id = location.id.value.toString,
    version = location.version.value,
    locationName = location.locationName.value,
    locationAbbreviation = location.locationAbbreviation.value,
    countryId = location.countryID.value.toString,
    createDate = location.createDate.asTimestamp,
    updateDate = location.lastUpdate.map(_.asTimeStamp)
  )

}
