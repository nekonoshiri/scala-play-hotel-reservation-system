package models

import javax.inject.{Inject, Singleton}

import org.apache.pekko.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

/** データベースアクセスを行う際に用いる実行コンテキスト。
  *
  * この実行コンテキストの設定は、`conf/application.conf` 設定ファイル内の `database.dispatcher` にあります。
  *
  * @note
  *   Anorm + JDBC を利用している場合はデータベースアクセスがブロッキング処理になるため、[[CustomExecutionContext]]
  *   を利用してデフォルトの実行コンテキストとは異なる実行コンテキストを利用することが推奨されています。
  *
  * @see
  *   https://www.playframework.com/documentation/3.0.x/AccessingAnSQLDatabase
  */
@Singleton
class DatabaseExecutionContext @Inject() (system: ActorSystem)
    extends CustomExecutionContext(system, "database.dispatcher")
