package org.dka.books.quill.service

import com.typesafe.scalalogging.Logger
import org.dka.books.domain.model.fields.*
import org.dka.books.domain.model.item.*
import org.dka.books.quill.service.tables.Tables
import zio.*
import zio.internal.*
import zio.test.*
import zio.test.Assertion.*

import java.time.LocalDate


object JoinLoadSpec extends ZIOSpecDefault {
  private val logger = Logger(getClass.getName)

//  Runtime.setBlockingExecutor(Blocking.blockingExecutor)
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("AuthorBookLoad")(

    test("getAuthorsForBooks single query, first") {
      val now = java.lang.System.currentTimeMillis()
      for {
        id <- BookDaoService.getIds.head
        authors <- BookDaoService.getBookAuthorSummary(id)
      } yield {
        val time = java.lang.System.currentTimeMillis()
        logger.info(s"getAuthorsForBooks, id: $id, first query, first: ${time - now}")
        assertTrue(true)
      }
    },
    test("getAuthorsForBooks - concurrently") {
      val now = java.lang.System.currentTimeMillis()
      for {
        ids <- BookDaoService.getIds
        _ <- ZIO.foreachParDiscard(ids)(id => BookDaoService.getBookAuthorSummary(id))
      }
      yield {
        val time = java.lang.System.currentTimeMillis()
        logger.info(s"getAuthorsForBooks concurrently, ids: ${ids.length}, time: ${time - now}, avg: ${(time - now) / ids.length.toDouble}")
        assertTrue(ids.length == 10000)
      }
    },
      test("getAuthorsForBooks - sequential") {
        val now = java.lang.System.currentTimeMillis()
        for {
          ids <- BookDaoService.getIds
          _ <- ZIO.foreachDiscard(ids)(id => BookDaoService.getBookAuthorSummary(id))
        }
        yield {
          val time = java.lang.System.currentTimeMillis()
          logger.info(s"getAuthorsForBooks concurrently, ids: ${ids.length}, time: ${time - now}, avg: ${(time - now) / ids.length.toDouble}")
          assertTrue(ids.length == 10000)
        }
      }
  ).provide(
    BookDaoServiceImpl.layer,
    Tables.tablesLayer,
    QuillContext.quillLayer,
    QuillContext.dataSourceLayer
  )
}
