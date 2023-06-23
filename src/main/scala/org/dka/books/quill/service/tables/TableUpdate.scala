package org.dka.books.quill.service.tables

import java.sql.Timestamp

trait TableUpdate {

  def id: String

  def version: Int

  def createDate: Timestamp

  def updateDate: Option[Timestamp]

}
