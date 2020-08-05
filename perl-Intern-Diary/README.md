# Intern::Diary

## 起動
```
$ docker-compose up
```

アプリケーションと, データベース(MySQL)が起動します.

### テーブルの初期作成

`docker-compose up` の起動が落ち着いてから、1回だけ実行する。

```
$ docker-compose run --rm app script/init_db
```

### MySQL

`docker-compose up`で起動したMySQLへ, mysql クライアントでデータベースに接続するには, 次のようにします.

```
$ docker-compose exec db mysql
```

### コマンド実行

`docker-compose up` でコンテナが立ち上がっているとき, そのコンテナの中でコマンドを実行したいなら, 次のようにする.

```
$ docker-compose exec app [script]
```

## テスト

テスト用DBにテーブルを用意しておく必要がある.

```
$ docker-compose run --rm -e DATABASE_NAME=intern_diary_test app script/init_db
```

テストを実行するなら, `docker-compose up`でコンテナを立ち上げた上で, このようにするとよい.

```
$ docker-compose exec app carton exec -- prove -v t/engine/index.t
```

## API

### `$c`
- `Intern::Diary::Context`
- コンテキストという名が示すように、ユーザーからのリクエストにレスポンスを返すまでに最低限必要な一連のメソッドがまとめられている

### `$c->req`
- リクエストオブジェクトを返す
- [`Plack::Request`](http://search.cpan.org/dist/Plack/lib/Plack/Request.pm)を継承した`Intern::Diary::Request`

### `$c->req->parameters->{$key}`
- `$key`に対応するリクエストパラメーターを返す
- クエリパラメーターやルーティングによって得られたパラメーターなど全てが対象となる

### `$c->dbh`
- データベースのハンドラを返す
- [`DBIx::Sunny`](http://search.cpan.org/dist/DBIx-Sunny/lib/DBIx/Sunny.pm)

### `$c->html($template_file, $parameters)`
- ファイル名とテンプレート変数を受け取ってレンダリングされたHTMLをレスポンスに設定する
```perl
$c->html('index.html', { foo => $bar });
```

### `$c->json($object)`
- ハッシュリファレンスを受け取ってJSON文字列化したものをレスポンスに設定する
```perl
$c->json({ spam => $egg });
```

### `$c->throw_redirect($url)`
- 大域脱出して渡されたURLにリダイレクトする
```perl
$c->throw_redirect('/');
```

### `$c->res`
- レスポンスオブジェクトを返す
- [`Plack::Response`](http://search.cpan.org/dist/Plack/lib/Plack/Response.pm)

## 設定
環境変数によって設定できる. docker-composeを利用している場合, 環境変数はdocker-compose.ymlで設定していることに注意.
デフォルトの値があるものは, `( ... )`で示す.

| 変数名            | 意味 |
| ----------------- | ---- |
| PORT              | アプリケーションが接続を受け付けるポート(3000) |
| ORIGIN            | アプリケーションが動くスキームとホスト名(`http://localhost:[PORT]`) |
| DATABASE_HOST     | MySQLに接続するためのホスト名(localhost) |
| DATABASE_USER     | MySQLに接続するためのユーザ名(root) |
| DATABASE_PASSWORD | MySQLに接続するためのパスワード |
| DATABASE_NAME     | MySQLに接続するためのデータベース名(intern_diary) |
