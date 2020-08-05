# Intern-Bookmark

## 準備

### OAuthアプリケーションの登録

scala-Intern-Bookmark ははてなの OAuth を利用しているため、事前にアプリケーション登録が必要です。

[Consumer key を取得して OAuth 開発をはじめよう - Hatena Developer Center](http://developer.hatena.ne.jp/ja/documents/auth/apis/oauth/consumer) を読んで、はてなにアプリケーションを登録してください。

`.env` というファイルを作って、以下のようにコンシューマキー、コンシューマシークレットを記述してください。

```
HATENA_OAUTH_CONSUMER_KEY=***
HATENA_OAUTH_CONSUMER_SECRET=***
```

### docker-compose を使う（オススメ）

```sh
% docker-compose up -d
Creating network "scalainternbookmark_default" with the default driver
Creating scalainternbookmark_db_1 ...
Creating scalainternbookmark_db_1 ... done
Creating scalainternbookmark_sbt_1 ...
Creating scalainternbookmark_sbt_1 ... done
>
```

この方法を使う場合、データベースの準備は不要。`sbt` を手元で起動する代わりに `docker-compose exec` してください。

```sh
% docker-compose exec sbt sbt
> test # など
```

`sbt`との接続を解除するには，`--detach-keys`で指定したキー(指定しなかった場合は`Ctrl-p` `Ctrl-q`)を押下してください。`sbt`コンテナを止めることなくシェルに戻ることができます．

```sh
> [Ctrl-d]
% # シェルに戻れる
```

mysql クライアントでデータベースに接続するには、以下のようにします。

```sh
% docker-compose exec db mysql
```

### docker-compose を使わない

#### sbt

[sbtのダウンロードページ](http://www.scala-sbt.org/download.html) などを参考にインストールしましょう。Macの場合は以下のようにすればOK。

```sh
$ brew install sbt
```

#### データベース

MySQLを使いますのでインストールします。

```sh
$ brew install mysql
$ ln -fs $(brew --prefix mysql)/homebrew.mxcl.mysql.plist ~/Library/LaunchAgents/
$ launchctl load ~/Library/LaunchAgents/homebrew.mxcl.mysql.plist
```

データベースとテーブル定義を読み込ませます。

```sh
$ mysqladmin create internbookmark
$ mysqladmin create internbookmark_test
$ cd /path/to/Intern-Bookmark-scala
$ cat db/schema.sql | mysql -uroot internbookmark
$ cat db/schema.sql | mysql -uroot internbookmark_test
```

## 実行

```sh
$ sbt
> run list
> run add http://www.hatena.ne.jp 便利なページ
> run delete http://www.hatena.ne.jp
```

mainクラスが複数あるので、実行時にどれを起動するか選択する必要があります。

## Webサーバの起動

```sh
$ sbt
> ~jetty:start
```

`~` はソースコードを監視して、変更があった場合に続くコマンドを実行します。監視を終了するには `enter`。

## テスト

```sh
$ sbt
> test
```

## モジュール

- internbookmark
  - cli
    - BookmarkCli CLI の実装
  - model データモデル
    - Bookmark
    - Entry URLと対応
    - User
  - repository データモデルをDBやファイルシステムなどに記録して操作するためのサービス
    - Bookmarks Bookmarkのリポジトリ
    - Entries Entryのリポジトリ
    - Users Userのリポジトリ
    - Identifier Idを作ってくれる
    - TitleExtractor urlに対応するWebサイトのタイトル情報を取得してくれるサービスのtrait
    - TitleExtractorImpl TitleExtractorの実装
  - service アプリケーションのコアロジック
    - Json データモデルのJSON表現を管理
    - BookmarkCLIApp ブックマークの主たる機能を表現したアプリケーションクラス
    - Error アプリケーション層で使うエラー定数
