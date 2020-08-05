# ScalaによるWebアプリケーション開発

Scalatra による実例を通じて、Webアプリケーション開発の雰囲気を掴みましょう。

## 目次

- Scalatra
- scala-Intern-Bookmark
- ブックマーク一覧を作ってみよう
  - URL 設計
  - CLI 版との比較
  - コントローラを書こう
  - ビューを書こう
  - Twirl 入門
  - テストを書こう
- 他の機能も作ってみよう
- セキュリティ

## Scalatra

http://scalatra.org/

Ruby の Sinatra ライクな WAF (Web Application Framework) です。Scala で簡単な Web アプリケーションを書くのに便利です。これから Scalatra を使って、Web アプリケーションを作っていきましょう。

完成形のお手本が `scala-Intern-Bookmark` にあります。この資料では説明しませんが、はてな OAuth によるユーザー認証まで作りこんでいるので資料のコードと若干内容が異なります。

## scala-Intern-Bookmark

Scalatra を利用して作成した Web アプリの例です。

### ディレクトリ構成

    .
    ├── Dockerfile
    ├── README.md
    ├── build.sbt
    ├── db
    │   ├── docker
    │   │   └── init.sh
    │   └── schema.sql
    ├── docker-compose.yml
    ├── project
    │   ├── build.properties
    │   └── plugins.sbt
    ├── script
    │   ├── entrypoint
    │   └── setup.sh
    └── src
        ├── main
        │   ├── resources
        │   │   ├── application.conf
        │   │   └── logback.xml
        │   ├── scala
        │   │   ├── HatenaOAuth.scala
        │   │   ├── JettyLauncher.scala
        │   │   ├── ScalatraBootstrap.scala
        │   │   └── internbookmark
        │   │       ├── cli
        │   │       │   └── BookmarkCLI.scala
        │   │       ├── model
        │   │       │   ├── Bookmark.scala
        │   │       │   ├── Entry.scala
        │   │       │   └── User.scala
        │   │       ├── repository
        │   │       │   ├── Bookmarks.scala
        │   │       │   ├── Context.scala
        │   │       │   ├── Entries.scala
        │   │       │   ├── Identifier.scala
        │   │       │   └── Users.scala
        │   │       ├── service
        │   │       │   ├── BookmarkApp.scala
        │   │       │   ├── Error.scala
        │   │       │   ├── Json.scala
        │   │       │   ├── TitleExtractor.scala
        │   │       │   ├── TitleExtractorImpl.scala
        │   │       │   └── package.scala
        │   │       └── web
        │   │           ├── AppContextSupport.scala
        │   │           ├── BookmarkAPIWeb.scala
        │   │           └── BookmarkWeb.scala
        │   ├── twirl
        │   │   └── internbookmark
        │   │       ├── add.scala.html
        │   │       ├── delete.scala.html
        │   │       ├── edit.scala.html
        │   │       ├── list.scala.html
        │   │       └── wrapper.scala.html
        │   └── webapp
        │       ├── WEB-INF
        │       │   └── web.xml
        │       ├── javascripts
        │       │   └── main.js
        │       └── stylesheets
        │           └── default.css
        └── test
            ├── resources
            │   └── test.conf
            └── scala
                └── internbookmark
                    ├── helper
                    │   ├── Factory.scala
                    │   ├── SetupDB.scala
                    │   ├── UnitSpec.scala
                    │   └── WebUnitSpec.scala
                    ├── service
                    │   └── BookmarkAppSpec.scala
                    └── web
                        └── BookmarkWebSpec.scala

src/main/scala以下の重要な項目としては以下のとおり。
- `src/main/scala/ScalatraBootstrap.scala`
  - Scalatraの起動をおこなってるファイル
- `src/main/resources/application.conf`
  - アプリケーションの設定はここ
- `src/main/scala/internbookmark/web/BookmarkWeb.scala`
  - エンドポイントごとの処理を書くとこ
- モデル（前の講義で見たはず）
  - `src/main/scala/internbookmark/service`
  - `src/main/scala/internbookmark/model`
  - `src/main/scala/internbookmark/repository`
  - service, repository, model を合わせて俗にいうモデル

### OAuth アプリケーションの登録

scala-Intern-Bookmark ははてなの OAuth を利用しているため、事前にアプリケーション登録が必要です。

[Consumer key を取得して OAuth 開発をはじめよう - Hatena Developer Center](http://developer.hatena.ne.jp/ja/documents/auth/apis/oauth/consumer) を読んで、はてなにアプリケーションを登録してください。

`build.sbt` と同じディレクトリに `.env` というファイルを作って、以下のようにコンシューマキー、コンシューマシークレットを記述してください。

```
HATENA_OAUTH_CONSUMER_KEY=***
HATENA_OAUTH_CONSUMER_SECRET=***
```



余談: [dotenv](https://github.com/bkeepers/dotenv) というライブラリ／ツールで採用されている形式です

## docker-compose & sbt による開発

開発には docker-compose を使います！

```sh
% docker-compose up -d
Creating network "scalainternbookmark_default" with the default driver
Creating scalainternbookmark_db_1 ...
Creating scalainternbookmark_db_1 ... done
Creating scalainternbookmark_sbt_1 ...
Creating scalainternbookmark_sbt_1 ... done
>
```

`sbt` を手元で起動する代わりに `docker attach` してください。

```sh
% docker attach --detach-keys='ctrl-d' $(docker-compose ps -q sbt)
# 何も応答ないですが [enter] などすると sbt プロンプトが表示されます
> ~jetty:start # HTTP サーバを起動、ソースに変更があったら再起動
```

ブラウザで http://localhost:8080/ にアクセス！

mysql クライアントでデータベースに接続するには、以下のようにします。

```sh
% docker-compose exec db mysql
```

## ブックマーク一覧を作ってみよう

### URL 設計

実装に入る前に、まずは URL を設計することにしましょう。最初にインタフェース設計から入ると作りやすいことは多いです。

### CLI 版との比較

前の章でみた internbookmark CLI での機能は以下のとおりでしたね。

- 一覧 (list)
- 表示
- 作成 (add)
- 削除 (delete)

これらに対応する URL を、今回は以下のように設計します。

| パス                                       | メソッド | 動作                                   |
| ---------------------------------------- | ---- | ------------------------------------ |
| /bookmarks                               | GET  | ブックマーク一覧                             |
| /bookmark/:id                            | GET  | ブックマークの permalink (:idは追加時に採番される ID) |
| /bookmark/add?url={url}&comment={comment} | POST | ブックマークの追加                            |
| /bookmark/:id/delete                     | POST | ブックマークの削除                            |

### コントローラを書こう

上記の URL 設計におけるブックマーク一覧 (`/bookmarks`) を例として、コントローラを実装していきます。

#### まずは Hello World から

`src/main/scala/internbookmark/web/BookmarkWeb.scala` に、リクエストメソッド・パスと、対応する処理(Scalatra では合わせて action と呼びます) を書きます。

```scala
  get("/") {
    Ok("Welcome to the Hatena world!")
  }
```

[`Ok`](http://scalatra.org/apidocs/2.5/org/scalatra/Ok$.html) は `200 OK` 応答を返すための API。ブラウザで http://localhost:8080 にアクセスすると、`Ok()` に渡した文字列がそのまま表示されます。

もう少し大きなアプリケーション用の WAF だと、ルーティング設定 (URL やメソッドに対する処理のマッピング) とコントローラーは分離されているものが多いです。Scalatra は手軽な WAF なので、ルーティング設定とコントローラが一緒になっています。

例えば、[Mackerel](https://mackerel.io/) で使っている [Play Framework](https://www.playframework.com/) では、リクエストメソッド・パスに対応する object のメソッドを routes ファイルに書くというふうになっています。

```
GET    /           Application.index
GET    /bookmarks  Bookmarks.list
```

#### ブックマーク一覧のコントローラを作る

- `> run list` に対応
- コントローラでやること
  1. リクエストしているユーザを取得
  2. モデル (repository) から、ユーザのブックマーク一覧を取得
  3. 取得したブックマーク一覧をビューに渡し、HTML として生成

```scala
// src/main/scala/internbookmark/web/BookmarkWeb.scala

get("/bookmarks") {
  // (1) ユーザは決め打ち
  val currentUserName = "motemen"
  // (1) ユーザを取得
  val currentUser = repository.Users.findOrCreateByName(currentUserName)
  // (2) ブックマーク一覧を取得
  val list = repository.Bookmarks.listAll(currentUser).toList
  // (3) ブックマーク一覧をビューに渡す
  internbookmark.html.list(list)
}
```

- `internbookmark.html.list` とは
    - `src/main/twirl/internbookmark/list.scala.html` (後述) から生成された Scala 実装

#### モデルにロジックを切り出す

- 開発してるとコントローラにどんどん処理を書きたくなってくる
  - → **Fat Controller** と呼ばれるアンチパターン
- 実現したい処理から、入出力の形式によらない部分を抜き出してみよう
  - それ他の入り口 (CLI とか) からも使いたくない？ と考えると :+1:
    - ユーザのアクセスによらず定時実行する処理、などもよくあります
- ユーザが主体となってブックマークの情報にアクセスするパターンは頻出
  - ユーザー情報を保持する class のメソッドとして、アプリケーションロジックを実装してみる

```scala
// src/main/scala/internbookmark/service/BookmarkApp.scala

package internbookmark.service

import internbookmark.model.{Bookmark, User}
import internbookmark.repository

class BookmarkApp(currentUserName: String) {
  def currentUser(implicit ctx: Context): User = {
    repository.Users.findOrCreateByName(currentUserName)
  }
  
  def list()(implicit ctx: Context): List[Bookmark] =
    repository.Bookmarks.listAll(currentUser).toList
}
```

すると先ほどの `BookmarkWeb.scala` は以下のようにできる。CLI からも使いまわせて便利そう。

```Scala
// src/main/scala/internbookmark/web/BookmarkWeb.scala

get("/bookmarks") {
  val currentUserName = "motemen"
  val app = new BookmarkApp(currentUserName)
  internbookmark.html.list(app.list())
}
```

- 個別の利用用途を足がかりに、アプリケーションが実現すべきことをモデリングし、洗練させていく
  - ちゃんと作るときは実装する前に考えます
- いわゆる MVC (Model-View-Controller) について
  - Controller
    - ユーザの入力 (ここでは HTTP リクエスト) を M につなぐ
    - モデルからの返答を V に渡し、ユーザへの出力 (HTTP レスポンス) とする
  - View
    - ユーザが手に取れるものを生成
  - Model
    - アプリケーションの本体
- Intern-Bookmark は M がさらにレイヤ化されている
  - service
    - アプリケーションロジック。やりたいことはここに表現されている
  - model
    - エンティティ (アプリケーション中の登場人物) の表現
    - データベースへのアクセスは行わない
  - repository
    - 永続化層 (DB) からエンティティを出し入れする
- 責務の集約と分散

  - レイヤ間のインタフェースを狭く保つ
    - レイヤの事情を別のレイヤに漏れ出させない
      - 変更を局所化させる
    - (Scala においては) レイヤ間を trait でつないでおく
      - 関連するレイヤの実装を置き換えられるのでテスト・変更しやすい

#### 動的な URL の指定

Scalatra のアクションの URL パスには、単純な文字列だけではなくパターンを指定できます。例えば以下のようにidを取得することができます。

http://scalatra.org/guides/2.5/http/routes.html

```scala
get("/bookmarks/:id") {
  val app = createApp()
  (for {
    // GET /bookmarks/42 された場合、"42" が得られる
    id <- params.get("id").toRight(BadRequest()).right
  ...
}
```

以下の書籍などを参考にして、URL の設計をしてみましょう。

**参考書籍**
- Webを支える技術 5章

### ビューを書こう

- `src/main/twirl/internbookmark/list.scala.html` が `internbookmark.list()` になると言ったな
  - `target/scala-2.11/twirl/main/internbookmark/html/list.template.scala` 
- 生成しているのは sbt-twirl というプラグイン
  - ref: `project/plugins.sbt`


- このファイルは [Twirl](https://www.playframework.com/documentation/ja/2.4.x/ScalaTemplates) (とわ〜る) という Scala ベースのテンプレート形式になっている

### Twirl 入門

- [Play Framework](https://www.playframework.com/) 標準のテンプレートエンジン
- テンプレートは Scala のソースコードに変換される
  - 静的型付けされているので、アプリケーション同様の安心感
  - ちょっといじっただけでも再コンパイルの必要があり、面倒もある
- **The magic '@' character**
  - `@`以降に Scala の式が書ける。終了位置はよしなに判断してくれる
    - `Hello @customer.name!`

#### ビューの引数

- コントローラ側の呼び出し時に引数を渡せる
- もちろん型を書く

```
@(bookmarks: List[internbookmark.model.Bookmark])
```

#### 繰り返し処理

- リストに対する繰り返し
- `@` と `for` と `(` の間はスペースをいれないように注意 (これは`if`などでも同様)

```html
@for(bookmark <- bookmarks) {
  <li><a href="@bookmark.entry.url">@bookmark.entry.title</a> - @bookmark.comment - @bookmark.createdAt.toStrin<a href="/bookmarks/@bookmark.id/edit">edit</a> <a href="/bookmarks/@bookmark.id/delete">delete</a></li>
}
```

#### 分岐処理

```html
@if(bookmarks.isEmpty) { ... } else { ... }
```

#### 外部テンプレートからの読み込み

```html
@widget.socialButtons()
```

`widget/socialButtons.scala.html` を読み込む。コントローラーからの呼出し同様にテンプレート名を指定してそのまま呼び出せます。

#### Wrapper パターン

通常、Web ページの大枠 (ヘッダやフッタ、サイドバーとか) は決まっているもの。共通部分を wrapper として切り出します。layout と呼んだりもする。

```html
// wrapper.scala.html
@(title: String)(content: Html)
<html>
  <head>
    <title>@title</title>
  </head>
  <body>
@content
  </body>
</html>
```

以下のようにして呼び出します。

```html
@wrapper(title = "Hello") {
  <p>Hello World!</p>
}
```

`{}` の内部が、wrapper.scala.html に `content` 引数として渡されます。

#### コメント

```html
@* コメント *@
```

参考
- Twirl: https://github.com/playframework/twirl
- https://www.playframework.com/documentation/2.5.x/ScalaTemplates
- https://www.playframework.com/documentation/2.5.x/ScalaTemplateUseCases

#### ブックマーク一覧のビューを作る

コントローラで指定したテンプレートは `src/main/twirl/internbookmark/list.scala.html` でした。

```html
@(bookmarks: List[internbookmark.model.Bookmark])
@wrapper("Bookmarks"){
  <a href="/bookmarks/add">Add</a>
  <ul>
  @for(bookmark <- bookmarks){
    <li><a href="@bookmark.entry.url">@bookmark.entry.title</a> - @bookmark.comment - @bookmark.createdAt.toString <a href="/bookmarks/@bookmark.id/edit">edit</a> <a href="/bookmarks/@bookmark.id/delete">delete</a></li>
  }
  </ul>
}
```

- コントローラから渡した `bookmarks` に、通常の Scala のコードのようにアクセスできる
  - Scala の制御構造と HTML が混在していますが、丁寧に読んでいけば難しくはない
- 自動で HTML エスケープしてくれます

### テストを書こう

機能を作るときにはテストを書きましょう。Scalatra には [ScalaTest](http://www.scalatest.org/) というテストフレームワーク対応の実装が用意されているので、これを利用します。

http://scalatra.org/guides/2.5/testing/scalatest.html

```scala
// BookmarkWeb (ウェブ層) をテストしやすいように一部の実装をオーバライドする
class BookmarkWebForTest extends BookmarkWeb {
  override def createApp()(implicit request: HttpServletRequest): BookmarkApp = new BookmarkApp(currentUserName()) {
    // BookmarkApp (サービス層) も継承しちゃう。外部 HTTP アクセスをしないような実装で置き換える
    override val entriesRepository = EntriesForTest
  }

  // クッキーに入っている値でセッション状態を上書きできるようにする
  override def currentUserName()(implicit request: HttpServletRequest): String = {
    request.cookies.getOrElse("USER", throw new IllegalStateException())
  }
}

class BookmarkWebSpec extends WebUnitSpec with SetupDB {
  // BookmarkWebForTest をテスト対象とする
  addServlet(classOf[BookmarkWebForTest], "/*")

  val testUserName = Random.nextInt().toString
  def testUser()(implicit ctx: repository.Context): User =
    repository.Users.findOrCreateByName(testUserName)
  def withUserSessionHeader(headers: Map[String, String] = Map.empty) = {
    headers + ("Cookie" -> s"USER=$testUserName;")
  }

  describe("BookmarkWeb") {
    it("should redirect to login page for an unauthenticated access") {
      // get() などでリクエストに対するレスポンスをテストできる
      get("/bookmarks") {
        status shouldBe 302
        header.get("Location") should contain("/auth/login")
      }
    }

    it("should redirect to the list page when the top page is accessed") {
      get("/",
        headers = withUserSessionHeader()
      ) {
        status shouldBe 302
        header.get("Location") should contain ("/bookmarks")
      }
    }

    it("should show list of bookmarks") {
      get("/bookmarks",
        headers = withUserSessionHeader()
      ) {
        status shouldBe 200
      }
    }
  }
}
```

テストは以下のようにして動かします。特定のSpecを動かしたい場合は、testOnlyを使いましょう。

```bash
$ sbt
> test
> testOnly internbookmark.web.BookmarkWebSpec
```

## 一旦おさらい

Scalatra での開発の流れは

1. 機能のエンドポイント (リクエストメソッド、パス、パラメタなど) を決める
2. アクション (リクエストメソッド、パス、実装) を定義する
3. アクションの中では、モデルを呼び出して結果をビューに渡す
4. ビューを書く

## 他の機能も作ってみよう

今度はブックマーク追加を作ってみましょう。要件は以下のようにしてみます。

- `GET /bookmark/add`: ブックマーク追加のフォーム
- `POST /bookmark/add `: ブックマーク追加、追加後ブックマーク一覧ページへ

### コントローラを作る

```scala
  get("/bookmarks/add") {
    internbookmark.html.add()
  }

  post("/bookmarks/add") {
    val app = createApp()
    (for {
      url      <- params.get("url").toRight(BadRequest()).right
      bookmark <- app.add(url, params.getOrElse("comment", ""))
      	.left.map(_ => InternalServerError())
      	.right
    } yield bookmark) match {
      case Right(bookmark)   => Found(s"/bookmarks")
      case Left(errorResult) => errorResult
    }
  }
```

- パラメータを取得したい時は `params.get("url")` などとする
  - URL のクエリストリング (`?url={url}`)、POST された body、ルートパラメタ (`/bookmarks/:id`) などにまとめてアクセス可能
- `Found` (302) で HTTP リダイレクト

#### Either を使ったエラー処理

- エラー処理には [Either](http://www.scala-lang.org/api/2.12.0/scala/util/Either.html) を使うと便利
  - Right に正常系、Left に異常系
- 正常系と異常系での処理の切り分けをすっきり書けます
  - アプリケーション層が返すエラーに応じて、クライアントに応答するエラーの内容を決める
  - アプリケーション層が HTTP レスポンスを決めるのではなく、コントローラでマッピングするのがよい (レイヤの分離)

### ビューを書く

- `GET /bookmark/add` されたら投稿フォームを表示したい

```html
@()
@wrapper("Add Bookmark"){
  <form action="/bookmarks/add" method="POST">
    <dl>
      <dt><label for="url">URL:</label></dt><dd><input type="text" name="url" size="80" value=""/></dd>
      <dt><label for="comment">Comment:</label></dt><dd><input type="text" name="comment" size="80"  value=""/></dd>
    </dl>
    <input type="submit" value="Add"/> <a href="/bookmarks">List</a>
  </form>
}
```

- `/bookmark/add` に POST するフォーム
  - `<input name="url">` や `<input name="comment">` が POST body として送信される

他の機能はこれまで説明した機能を用いて実装できるので、scala-Intern-Bookmarkを見てください！

## この章のまとめ

- Scalatra による開発の流れを見ました
  - コントローラ (アクション) とビュー
- コントローラの実装指針も簡単に見ました

## セキュリティについて

### XSS (cross site scripting)

たとえばブックマークのコメントをエスケープせずに HTML として描画してみる:

```HTML
@* twirlではHtml()を通すとhtmlエスケープされなくなる *@
<p>@Html(bookmark.comment)</p>
```

この場合以下のコメントに以下の文字が入っていると、JavaScript が実行されてしまう

```html
<script>alert('XSS')</script>
```

#### 対策

- 出力時に適切なエスケープをする、そのような実装を使う
  - Twirl は自動的にエスケープしてくれる
- `Html()` を使うと明示的にでエスケープをなくせる
  - あえて HTML タグをに出力したいときに使う
  - もちろん細心の注意が必要

### CSRF (cross site request forgery)

もっともシンプルな例: たとえば他の (悪意ある) ページで……

```html
<form action="http://internbookmark.example.com/bookmarks" method="POST">
  <input type="hidden" name="url" value="http://malicious.example.jp/">
  <button type="submit">100万円もらえるボタン</button>
</form>
```

 訪問者が思わずボタンをクリックしてしまうと、勝手に訪問者のブックマークが追加されてしまう！

#### 対策

- 自分のウェブサービスでしか生成できないような秘密の情報をリクエストに含め、POST リクエスト時に検証する
- Scalatra だと [CsrfTokenSupport](http://scalatra.org/apidocs/2.5/org/scalatra/CsrfTokenSupport.html) ってのがあります
  - mixin すると `csrfKey`、`csrfToken` が生えるので `<input type="hidden" name="@csrfKey" value="@csrfToken">` のようにフォームに埋め込む

# 課題3

- CLI 版 Intern-Diary を Web アプリケーションにしましょう

### (必須) 記事の表示

Web ブラウザで、記事を読めるようにしてください

- ブラウザで記事が読める
  - テンプレートをちゃんと使って HTML を書く
    - 最初は HTML でなくプレーンテキストを返すと楽
  - 良い URL 設計をしてみよう
- ページャが実装されている
  - たとえば 1 ページに 5 件ずつ記事が表示され、過去に遡れる
  - SQL の `OFFSET` / `LIMIT` と、`?page={page}` クエリパラメータを使ってみる

### (必須) 記事作成/編集/削除

- ブラウザで記事を書ける
- ブラウザで記事を更新できる
- ブラウザで記事を削除できる

### (オプション) 追加機能

以下のような追加機能を好きなだけ実装してみてください。

- ユーザの認証 (はてな/Twitter OAuth)
- HTML 以外の方法での出力、たとえばフィード (Atom, RSS)
- その他自分で思いついたものがあれば

### 注意

- 全然分からなかったらすぐに人に聞きましょう

## 参考: ユーザ認証層

- 以下のように、1つの Web サーバに複数のアプリケーションをマウントできる
- 認証処理の実装自体は HatenaOAuth.scala 参照のこと

```scala
// src/main/scala/ScalatraBootstrap.scala
import internbookmark._
import jp.ne.hatena.intern.scalatra.HatenaOAuth
import org.scalatra._
import javax.servlet.ServletContext
import internbookmark.service.Context

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext): Unit = {
    Context.setup("db.default")
    context.mount(new internbookmark.web.BookmarkWeb, "/*")
    context.mount(new HatenaOAuth, "/auth")
  }

  override def destroy(context: ServletContext): Unit = {
    Context.destroy()
  }
}
```

## 参考資料

- http://scalatra.org/2.5/guides/
- https://www.playframework.com/documentation/ja/2.4.x/ScalaTemplates

<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/2.1/jp/"><img alt="クリエイティブ・コモンズ・ライセンス" style="border-width:0" src="http://i.creativecommons.org/l/by-nc-sa/2.1/jp/88x31.png" /></a><br />この作品 は <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/2.1/jp/">クリエイティブ・コモンズ 表示 - 非営利 - 継承 2.1 日本 ライセンスの下に提供されています。</a>
