package org.dka.books.quill.service

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables
import zio.*
import zio.test.*
import zio.test.Assertion.*

import java.time.LocalDate

object AuthorBookSummarySpec extends ZIOSpecDefault {

  private val austenId = ID.build("14523580-12a5-448a-8250-62f1b83e58b5")

  private val grimmsId = ID.build("e1de1c95-19e5-4df6-aa49-7c1f7b1d1868")

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("AuthorBookSummary")(
    test("by bookId (seed)") {
      (for {
        summaries <- BookDaoService.getBookAuthorSummary(grimmsId)
      } yield {
        assertTrue(
          summaries.size == 2
        )
      })
        .provide(
          BookDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    },
      test("by authorId (seed)") {
      (for {
        summaries <- BookDaoService.getAuthorBookSummary(austenId)
      } yield {
          assertTrue(
          summaries.size == 5
        )
      })
        .provide(
          BookDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    }
  )

}
