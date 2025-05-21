package models

import anorm.SQL
import javax.inject.Inject
import play.api.db.Database
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * ベッドタイプのリポジトリ。
  */
class BedTypeRepository @Inject() (db: Database) (
  // Future { ... } 内で DatabaseExecutionContext を実行コンテキストとして利用する
  using DatabaseExecutionContext
):
  /**
    * ベッドタイプを挿入します。挿入時に ID が振られます。ID が振られたベッドタイプを返します。
    *
    * @param bedType 挿入するベッドタイプ。
    * @return 挿入し、ID が振られたベッドタイプ。
    */
  def insert(bedType: InitialBedType): Future[IdentifiedBedType] =
    Future {
      db.withConnection { implicit connection =>
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
