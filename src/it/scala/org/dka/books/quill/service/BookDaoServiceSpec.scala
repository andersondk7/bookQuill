package org.dka.books.quill.service

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables
import zio.*
import zio.test.*
import zio.test.Assertion.*

import java.time.LocalDate

object BookDaoServiceSpec extends ZIOSpecDefault {
  // note:  createDate will not be the same
  private val janeEyre = Book(
    ID.build("a310df63-d54a-4f11-801e-44a3796cedfa"),
    Version.build(1),
    Title.build("Jane Eyre"),
    Price.build(BigDecimal(11.89)),
    Some(PublisherID.build("48bc3e2f-8cc0-4187-8b5a-d3e4cb871c78")),
    PublishDate.build(Some(LocalDate.of(1847, 10, 19)))
  )

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("BookDao") (

    test("book by name (seed)") {

      (for {
        books <- BookDaoService.get(janeEyre.title)
      } yield {
        assertTrue(
          books.size == 1,
          books.head.copy(createDate = janeEyre.createDate) == janeEyre
        )
      })
        .provide(
          BookDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    },

    test("book by id (seed)") {

      (for {
        books <- BookDaoService.get(janeEyre.id)
      } yield {
        assertTrue(
          books.size == 1,
          books.head.copy(createDate = janeEyre.createDate) == janeEyre
        )
      })
        .provide(
          BookDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    },
    test("book by id (create)") {
      val jenny = janeEyre.copy(id = ID.build("1fc7222d-e67d-4c29-8a8e-7b08e2f8581e"))

      (for {
        inserted <- BookDaoService.create(jenny)
        books <- BookDaoService.get(jenny.id)
        deleted <- BookDaoService.delete(jenny.id)
      } yield {
        assertTrue(
          books.size == 1,
          books.head.copy(createDate = jenny.createDate) == jenny,
          deleted == jenny.id
        )
      })
        .provide(
          BookDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    },

      test("all books count") {
        (for {
        count <- BookDaoService.getIds
      }  yield {
        assertTrue(count.size == 110)
      }).provide(
        BookDaoServiceImpl.layer,
        Tables.tablesLayer,
        QuillContext.quillLayer,
        QuillContext.dataSourceLayer
      )
    }

  )

}
