package org.dka.books.quill.service

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables
import zio.*
import zio.test.*
import zio.test.Assertion.*

import java.time.LocalDate

object LocationDaoServiceSpec extends ZIOSpecDefault {
  // note:  createDate will not be the same
  private val dublin = Location(
    ID.build("52e8b846-a068-4847-8223-b156c356a70a"),
    Version.build(1),
    LocationName.build("Dublin"),
    LocationAbbreviation.build("DBL"),
    CountryID.build("52e8b846-a068-4847-8223-b156c356a70a")
  )

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("LocationDao") (

    test("location by id (seed)") {

      (for {
        locations <- LocationDaoService.get(dublin.id)
      } yield {
        assertTrue(
          locations.size == 1,
          locations.head.copy(createDate = dublin.createDate) == dublin
        )
      })
        .provide(
          LocationDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    },
    test("location by id (create)") {
      val dublies = dublin.copy(id = ID.build("69ef9369-c8c2-4282-b38f-44b40faa6184"))

      (for {
        inserted <- LocationDaoService.create(dublies)
        locations <- LocationDaoService.get(dublies.id)
        deleted <- LocationDaoService.delete(dublies.id)
      } yield {
        assertTrue(
          locations.size == 1,
          locations.head.copy(createDate = dublies.createDate) == dublies,
          deleted == dublies.id
        )
      })
        .provide(
          LocationDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    }

  )

}
