package org.dka.books.quill.service

import zio.*
import zio.test.*
import zio.test.Assertion.*
import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables

object AuthorDaoServiceSpec extends ZIOSpecDefault {
  // note:  createDate will not be the same
  private val charlesDickens = Author(
    ID.build("6c5987cc-073d-4191-ba9c-9f745431d308"),
    Version.build(1),
    LastName.build("Dickens"),
    FirstName.fromOpt(Some("Charles")),
    LocationID.fromOpt(Some("424c4dcf-7098-4694-a8ff-6c08d7ade6d2")),
  )

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("AuthorsDao") (

    test("author by name (seed)") {

      (for {
        authors <- AuthorDaoService.getByLastName(charlesDickens.lastName.value)
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

    test("author by id (seed)") {

      (for {
        authors <- AuthorDaoService.get(charlesDickens.id)
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
    test("author by id (create)") {
      val charlie = charlesDickens.copy(id = ID.build("d15e96af-2bce-4c88-80e9-757c9c260782"))

      (for {
        inserted <- AuthorDaoService.create(charlie)
        authors <- AuthorDaoService.get(charlie.id)
        deleted <- AuthorDaoService.delete(charlie.id)
      } yield {
        assertTrue(
          authors.size == 1,
          authors.head.copy(createDate = charlie.createDate) == charlie,
          deleted == charlie.id
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
        authors <- AuthorDaoService.getByLastName("Adams")
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
        count <- AuthorDaoService.getAll
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
