package org.dka.books.quill.service

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables
import zio.*
import zio.test.*
import zio.test.Assertion.*

import java.time.LocalDate


object JoinLoadSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("AuthorBookLoad")(
    test("authorBook (load)") {
      (for {
        authors <- AuthorDaoService.getAll
      } yield {
        assertTrue(authors.size == 1000)
      })
        .provide(
          AuthorDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    }
  )
}
