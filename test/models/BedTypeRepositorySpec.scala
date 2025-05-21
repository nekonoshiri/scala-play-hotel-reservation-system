package models

import anorm.{Macro, SQL}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.db.Database
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.inMemoryDatabase

class BedTypeRepositorySpec extends PlaySpec with GuiceOneAppPerTest with ScalaFutures:

  // test_db という名前のインメモリデータベースを使用してテストする
  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(inMemoryDatabase("test_db"))
      .build()

  "insert メソッド" must:
    "ベッドタイプに ID が振られて挿入され、挿入されたベッドタイプが返されること" in:
      val repository = app.injector.instanceOf[BedTypeRepository]
      val db = app.injector.instanceOf[Database]

      // TODO: Evolutions を使ってマイグレーションを行うように変更する予定。
      // とりあえず暫定処理としてここでテーブルを作成している
      // See: https://www.playframework.com/documentation/3.0.x/ScalaTestingWithDatabases#Applying-evolutions
      db.withConnection { implicit connection =>
        SQL("""
          CREATE TABLE bed_types (
            id IDENTITY,
            name TEXT NOT NULL,
            description TEXT NOT NULL
          )
        """).execute()
      }

      val testCases = Table(
        ("name", "description"),
        ("名前A", "説明A"),
        ("名前B", "説明B"),
        ("", ""),
      )

      forAll(testCases) { (name, description) =>
        val bedType = InitialBedType(name, description)

        whenReady(repository.insert(bedType)) { returned =>
          returned.id must not be 0

          db.withConnection { implicit connection =>
            val parser = Macro.parser[IdentifiedBedType]("id", "name", "description")
            val inserted = SQL("SELECT * FROM bed_types WHERE id = {id}").on("id" -> returned.id).as(parser.single)
            inserted mustBe returned
          }
        }
      }
