package org.dka.quill
import zio.*
import zio.test.*
import zio.test.Assertion.*

object AuthorsDaoSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("AuthorsDao") (
    test("always pass") {
      assertTrue(true)
    },

    test("author by name count") {
      (for {
        count <- AuthorDaoService.getAuthors("Adams")
      } yield {
        assertTrue(count.size == 2)
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
        assertTrue(count.size == 75)
      }).provide(
        AuthorDaoServiceImpl.layer,
        Tables.tablesLayer,
        QuillContext.quillLayer,
        QuillContext.dataSourceLayer
      )
    }

  )

}
