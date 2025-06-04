package models

import anorm.{Macro, SQL}
import java.sql.Connection
import javax.inject.Inject
import play.api.db.Database
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * ベッドタイプのリポジトリ。
  */
class BedTypeRepository @Inject() (db: Database) (
  // 非同期処理の実行コンテキストとして DatabaseExecutionContext を利用する
  using DatabaseExecutionContext
):
  /**
    * ベッドタイプの一覧を取得します。
    *
    * @return ベッドタイプの一覧。
    */
  def list(): Future[List[IdentifiedBedType]] =
    Future {
      db.withConnection { connection =>
        given Connection = connection
        val parser = Macro.parser[IdentifiedBedType]("id", "name", "description")
        SQL("SELECT * FROM bed_types").as(parser.*)
      }
    }.recoverWith {
      case NonFatal(e) =>
        Future.failed(new RuntimeException(s"ベッドタイプの一覧取得に失敗しました: ${e.getMessage}", e))
    }

  /**
    * ベッドタイプを挿入します。挿入時に ID が振られます。ID が振られたベッドタイプを返します。
    *
    * @param bedType 挿入するベッドタイプ。
    * @return 挿入し、ID が振られたベッドタイプ。
    */
  def insert(bedType: InitialBedType): Future[IdentifiedBedType] =
    Future {
      db.withConnection { connection =>
        given Connection = connection
        val sql = SQL(
          """
          INSERT INTO bed_types (name, description)
          VALUES ({name}, {description})
          """
        ).on(
          "name" -> bedType.name,
          "description" -> bedType.description,
        )
        val id = sql.executeInsert().getOrElse:
          throw new RuntimeException("ID の取得に失敗しました")
        bedType.withId(id)
      }
    }.recoverWith {
      case NonFatal(e) =>
        Future.failed(new RuntimeException(s"ベッドタイプの挿入に失敗しました: ${e.getMessage}", e))
    }

  // TODO: update する際は更新された件数をチェックして、0 件の場合はエラーとする
