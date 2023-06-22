package org.dka.books.quill.service.tables

trait DomainSupport[I, D] {

  def toDomain(item: I): D

  def fromDomain(domain: D): I

}
