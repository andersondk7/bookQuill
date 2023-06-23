package org.dka.books.quill.service

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables
import zio.*
import zio.test.*
import zio.test.Assertion.*

import java.time.LocalDate

object CountryDaoServiceSpec extends ZIOSpecDefault {
  // note:  createDate will not be the same
  private val sweden = Country(
    ID.build("1eeb9ef7-a543-4ae4-be40-b93e7ddd640c"),
    Version.build(1),
    CountryName.build("Sweden"),
    CountryAbbreviation.build("SWD")
  )

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("CountryDao") (

    test("country by id (seed)") {

      (for {
        countries <- CountryDaoService.get(sweden.id)
      } yield {
        assertTrue(
          countries.size == 1,
          countries.head.copy(createDate = sweden.createDate) == sweden
        )
      })
        .provide(
          CountryDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    },
    test("book by id (create)") {
      val sweedes = sweden.copy(id = ID.build("11c13cdf-ef16-4378-b3a0-276949dc6f23"))

      (for {
        inserted <- CountryDaoService.create(sweedes)
        countries <- CountryDaoService.get(sweedes.id)
        deleted <- CountryDaoService.delete(sweedes.id)
      } yield {
        assertTrue(
          countries.size == 1,
          countries.head.copy(createDate = sweedes.createDate) == sweedes,
          deleted == sweedes.id
        )
      })
        .provide(
          CountryDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    }

  )

}
