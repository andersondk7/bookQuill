package org.dka.books.quill.service.tables

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.Country

import java.sql.Timestamp

final case class CountryTable(
  override val id: String,
  override val version: Int,
  countryName: String,
  countryAbbreviation: String,
  createDate: Timestamp,
  override val updateDate: Option[Timestamp])
  extends TableUpdate

object CountryTable extends DomainSupport[CountryTable, Country] {

  override def toDomain(db: CountryTable): Country = Country(
    id = ID.build(db.id),
    version = Version.build(db.version),
    countryName = CountryName.build(db.countryName),
    countryAbbreviation = CountryAbbreviation.build(db.countryAbbreviation),
    lastUpdate = db.updateDate.map(UpdateDate.build),
    createDate = CreateDate.build(db.createDate)
  )

  override def fromDomain(country: Country): CountryTable = CountryTable(
    id = country.id.value.toString,
    version = country.version.value,
    countryName = country.countryName.value,
    countryAbbreviation = country.countryAbbreviation.value,
    createDate = country.createDate.asTimestamp,
    updateDate = country.lastUpdate.map(_.asTimeStamp)
  )

}
