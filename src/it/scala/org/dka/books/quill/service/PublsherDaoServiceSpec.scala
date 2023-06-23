package org.dka.books.quill.service

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables
import zio.*
import zio.test.*
import zio.test.Assertion.*

import java.time.LocalDate

object PublsherDaoServiceSpec extends ZIOSpecDefault {
  // note:  createDate will not be the same
  private val randomHouse = Publisher(
    id = ID.build("076931dc-1ed2-4abe-972c-d4aede099d70"),
    version = Version.build(1),
    publisherName = PublisherName.build("RandomHouse"),
    locationId = Some(LocationID.build("7e150434-d4db-4bda-8839-a4ab24cbf91b")),
    webSite = None
  )

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("PublisherDao") (

    test("publisher by id (seed)") {

      (for {
        publshers <- PublisherDaoService.get(randomHouse.id)
      } yield {
        assertTrue(
          publshers.size == 1,
          publshers.head.copy(createDate = randomHouse.createDate) == randomHouse
        )
      })
        .provide(
          PublisherDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    },
    test("publsher by id (create)") {
      val rHouse = randomHouse.copy(id = ID.build("80f49405-41cc-40bf-9206-a998c05705e8"))

      (for {
        inserted <- PublisherDaoService.create(rHouse)
        publishers <- PublisherDaoService.get(rHouse.id)
        deleted <- PublisherDaoService.delete(rHouse.id)
      } yield {
        assertTrue(
          publishers.size == 1,
          publishers.head.copy(createDate = rHouse.createDate) == rHouse,
          deleted == rHouse.id
        )
      })
        .provide(
          PublisherDaoServiceImpl.layer,
          Tables.tablesLayer,
          QuillContext.quillLayer,
          QuillContext.dataSourceLayer
        )
    }

  )

}
