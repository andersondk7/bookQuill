package org.dka.books.quill.service.tables

import java.sql.Timestamp

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*

final case class AuthorTable (
                               id: String,
                               version: Int,
                               lastName: String,
                               firstName: Option[String],
                               locationId: Option[String],
                               createDate: Timestamp,
                               updateDate: Option[Timestamp]
                             )



object AuthorTable {

  def toDomain(db: AuthorTable): Author = Author(
    id = ID.build(db.id),
    version = Version.build(db.version),
    lastName = LastName.build(db.lastName),
    firstName = FirstName.build(db.firstName),
    locationId = LocationID.fromOpt(db.locationId),
    createDate = CreateDate.build(db.createDate),
    lastUpdate = db.updateDate.map(UpdateDate.build)
  )
}