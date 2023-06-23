package org.dka.books.quill.service.tables

import org.dka.books.domain.model.fields.{FirstName, LastName, Title}
import org.dka.books.domain.model.query.BookAuthorSummary

final case class AuthorBook(
  authorId: String,
  bookId: String,
  authorOrder: Int) {}
