package org.dka.books.quill.service

import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import zio.{Unsafe, ZIO}

import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.duration.*

class BookDaoInteropSpec extends AnyFunSpec with Matchers {

  val runtime = zio.Runtime.default
  val delay = 2.minutes


  // note:  createDate will not be the same
  private val janeEyre = Book(
    ID.build("a310df63-d54a-4f11-801e-44a3796cedfa"),
    Version.build(1),
    Title.build("Jane Eyre"),
    Price.build(BigDecimal(11.89)),
    Some(PublisherID.build("48bc3e2f-8cc0-4187-8b5a-d3e4cb871c78")),
    PublishDate.build(Some(LocalDate.of(1847, 10, 19)))
  )

  describe("zio interop") {
    it ("should handle success") {
      val getBooks = BookDaoService.get(janeEyre.title)
        .provide(
        BookDaoServiceImpl.layer,
        Tables.tablesLayer,
        QuillContext.quillLayer,
        QuillContext.dataSourceLayer
      )
      val resultF = run[List[Book]](getBooks)
      val books: List[Book] = Await.result(resultF, delay)
      println(s"result: $books")

      books.length shouldBe 1
    }
  }

  private def run[A](effect: ZIO[Any, Throwable, A]) = Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe.runToFuture(effect)
  }
}
