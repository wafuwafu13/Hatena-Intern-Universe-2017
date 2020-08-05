# Intern::Bookmark

## 準備

### OAuthアプリケーションの登録

perl-Intern-Bookmark ははてなの OAuth を利用しているため、事前にアプリケーション登録が必要です。

[Consumer key を取得して OAuth 開発をはじめよう - Hatena Developer Center](http://developer.hatena.ne.jp/ja/documents/auth/apis/oauth/consumer) を読んで、はてなにアプリケーションを登録してください。

`.env` というファイルを作って、以下のようにコンシューマキー、コンシューマシークレットを記述してください。

```
HATENA_OAUTH_CONSUMER_KEY=***
HATENA_OAUTH_CONSUMER_SECRET=***
```

## 起動

```
$ docker-compose up
```

この方法を使う場合、MySQLの準備は不要。

## テーブルの初期作成

`docker-compose up`の起動が落ち着いてから、1回だけ実行する。

```
$ docker-compose run --rm app script/init_db
```

## MySQL

mysql クライアントでデータベースに接続するには、以下のようにする。

```
$ docker-compose exec db mysql
```

## テスト

テスト用DBにもテーブルを初期作成しておく。

```
$ docker-compose run --rm -e DATABASE_NAME=intern_bookmark_test app script/init_db
```

テストを実行する場合には以下のようにする。

```
$ docker-compose run --rm app carton exec -- prove -v t/object
```

## CLI版の実行

```
$ docker-compose run --rm app carton exec -- script/bookmark.pl your_user_name list
```

## Web版へのアクセス

`docker-compose up` している状態で http://localhost:3000 にアクセスする。

## 設定
環境変数によって設定できる。docker-composeを利用している場合、環境変数はdocker-compose.ymlで設定していることに注意。

| 変数名 | 意味 |
| ----- | ----- |
| SERVER_PORT | アプリケーションが接続を受け付けるポート |
| ORIGIN | ??? |
| DATABASE_HOST | MySQLに接続するためのホスト名 |
| DATABASE_USER | MySQLに接続するためのユーザ名 |
| DATABASE_PASSWORD | MySQLに接続するためのパスワード |
| DATABASE_NAME | MySQLに接続するためのデータベース名 |
| HATENA_OAUTH_CONSUMER_KEY | はてなOAuth認証のための consumer key |
| HATENA_OAUTH_CONSUMER_KEY | はてなOAuth認証のための consumer secret |

## OAuthの設定
- [Consumer keyの取得](http://developer.hatena.ne.jp/ja/documents/auth/apis/oauth/consumer)が必要
- `Intern::Bookmark::Config`の`hatena_oauth.consumer_key`と`hatena_oauth.consumer_secret`に取得したキーを設定
