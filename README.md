# scala-play-hotel-reservation-system

Scala + Play Framework を用いたホテル予約システムのサンプル。

## 必要なもの

- JDK (Java Development Kit) 21
- [sbt](https://www.scala-sbt.org/) 1.x
- (Optional) [Metals](https://scalameta.org/metals/): Scala の言語サーバ

## 実行できるコマンド

sbt がインストールされ使用できる状態であれば、リポジトリのルートディレクトリで
`sbt` コマンドを実行することで sbt シェルを起動できます。

sbt シェル内で使えるコマンドのうちのいくつかを以下に示します：

```sh
# アプリケーションを起動します。
# ブラウザで http://localhost:9000 にアクセスしてみてください。
#
# Note. Database 'default' needs evolution! と表示された場合は
# "Click here to apply this script now!" をクリックすることで
# データベースのマイグレーションを実行できます。
run

# テストを実行します。
test

# アプリケーションをコンパイルします。
compile

# Scalafmt を用いてコードをフォーマットします。
scalafmtAll # ソースコード等をフォーマット
scalafmtSbt # *.sbt ファイル等をフォーマット
```
