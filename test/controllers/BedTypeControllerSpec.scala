package controllers

import anorm.{BatchSql, NamedParameter}
import java.sql.Connection
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.db.Database
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.*
import play.api.test.Helpers.*

import models.IdentifiedBedType

class BedTypeControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting:

  // test_db という名前のインメモリデータベースを使用してテストする
  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(inMemoryDatabase("test_db"))
      .build()

  "index メソッド" must:
    "ベッドタイプの一覧を表示すること" in:
      val db = inject[Database]

      val bedTypes = List(
        IdentifiedBedType(1, "名前A", "説明A"),
        IdentifiedBedType(2, "名前B", "説明B"),
        IdentifiedBedType(3, "名前C", "説明C")
      )

      db.withConnection { connection =>
        given Connection = connection
        bedTypes.map(bt =>
          Seq[NamedParameter](
            "id" -> bt.id,
            "name" -> bt.name,
            "description" -> bt.description
          )
        ) match
          case Nil          => ()
          case head :: tail =>
            BatchSql(
              """
              INSERT INTO bed_types (id, name, description)
              VALUES ({id}, {name}, {description})
              """,
              head,
              tail*
            ).execute()
      }

      val request = FakeRequest(GET, "/bed-types")
      val response = route(app, request).get

      status(response) mustBe OK
      contentType(response) mustBe Some("text/html")

      val content = contentAsString(response)
      content must include("ベッドタイプ一覧")
      bedTypes.foreach: bt =>
        content must include(bt.id.toString)
        content must include(bt.name)
        content must include(bt.description)
