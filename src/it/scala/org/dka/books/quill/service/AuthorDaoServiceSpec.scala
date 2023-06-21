package org.dka.books.quill.service

import zio.*
import zio.test.*
import zio.test.Assertion.*
import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables

object AuthorDaoServiceSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("AuthorsDao") (

    test("author by name (seed)") {

      // note:  createDate will not be the same
      val charlesDickens = Author(
        ID.build("6c5987cc-073d-4191-ba9c-9f745431d308"),
        Version.build(1),
        LastName.build("Dickens"),
        FirstName.fromOpt(Some("Charles")),
        LocationID.fromOpt(Some("424c4dcf-7098-4694-a8ff-6c08d7ade6d2")),
      )
      (for {
        authors <- AuthorDaoService.getAuthors(charlesDickens.lastName.value)
      } yield {
        assertTrue(
          authors.size == 1,
          authors.head.copy(createDate = charlesDickens.createDate) == charlesDickens
        )

      })
        .provide(
          AuthorDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    },

    test("author by name count") {
      (for {
        authors <- AuthorDaoService.getAuthors("Adams")
      } yield {
        println(s"authors: $authors")
        assertTrue(authors.size == 2)
      })
        .provide(
        AuthorDaoServiceImpl.layer,
        Tables.tablesLayer,
        QuillContext.quillLayer,
        QuillContext.dataSourceLayer
      )
    },

      test("all author count") {
        (for {
        count <- AuthorDaoService.getAuthors
      }  yield {
        assertTrue(count.size == 71)
      }).provide(
        AuthorDaoServiceImpl.layer,
        Tables.tablesLayer,
        QuillContext.quillLayer,
        QuillContext.dataSourceLayer
      )
    }

  )

}
