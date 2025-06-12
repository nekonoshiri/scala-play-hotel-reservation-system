package models

import anorm.{BatchSql, Macro, NamedParameter, SQL}
import java.sql.Connection
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.TableDrivenPropertyChecks.*
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.db.Database
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.inMemoryDatabase

class BedTypeRepositorySpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with ScalaFutures:

  // test_db という名前のインメモリデータベースを使用してテストする
  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(inMemoryDatabase("test_db"))
      .build()

  /** データベースをリセットします。
    *
    * [[GuiceOneAppPerTest]] を用いる場合、テスト毎にデータベースはリセットされます。それ以外のタイミング、例えば `forAll`
    * 内でのテストケース毎にデータベースをリセットしたい場合等に利用できます。
    */
  private def resetDatabase(): Unit =
    val db = app.injector.instanceOf[Database]
    db.withConnection { connection =>
      given Connection = connection
      SQL("DELETE FROM bed_types").execute()
    }

  "list メソッド" must:
    "ベッドタイプの一覧が取得できること" in:
      val repository = app.injector.instanceOf[BedTypeRepository]
      val db = app.injector.instanceOf[Database]

      val testCases = Table(
        "bedTypes",
        List.empty,
        List(IdentifiedBedType(1, "名前A", "説明A")),
        List(
          IdentifiedBedType(1, "名前A", "説明A"),
          IdentifiedBedType(2, "名前B", "説明B"),
          IdentifiedBedType(3, "名前C", "説明C")
        )
      )

      forAll(testCases) { bedTypes =>
        resetDatabase()

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

        whenReady(repository.list()) { returned =>
          returned mustBe bedTypes
        }
      }

  "insert メソッド" must:
    "ベッドタイプに ID が振られて挿入され、挿入されたベッドタイプが返されること" in:
      val repository = app.injector.instanceOf[BedTypeRepository]
      val db = app.injector.instanceOf[Database]

      val testCases = Table(
        ("name", "description"),
        ("名前A", "説明A"),
        ("名前B", "説明B"),
        ("", "")
      )

      forAll(testCases) { (name, description) =>
        val bedType = InitialBedType(name, description)

        whenReady(repository.insert(bedType)) { returned =>
          returned.id must not be 0

          db.withConnection { connection =>
            given Connection = connection
            val parser =
              Macro.parser[IdentifiedBedType]("id", "name", "description")
            val inserted = SQL("SELECT * FROM bed_types WHERE id = {id}")
              .on("id" -> returned.id)
              .as(parser.single)
            inserted mustBe returned
          }
        }
      }
