# 1日目課題提出ディレクトリ

1日目の課題はこのディレクトリ以下にファイルを配置してください。

データモデリングの内容についてはこのディレクトリ内になんらかのドキュメントとしてコミットしてください。
形式はmarkdown, プレーンテキストなどが考えられますが、都合の良いフォーマットで記述してください。

日記システムのコードについては `src/main/scala/` 内に記述してください。
ライブラリについては `project/Build.scala` に追記してもらっても構いません。テストは`src/test/scala/` 以下に配置するとよいでしょう。

コンソールでの動作確認やテストの実行については事前課題でやったのと同じように `sbt console` や `sbt test` を使っていくとよいでしょう。イメージとしては以下のような感じです。
```console
$ sbt

> console
[info] Starting scala interpreter...
[info]
Welcome to Scala 2.11.11 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_45).
Type in expressions for evaluation. Or try :help.

scala> import hatena.intern.exercise._
import hatena.intern.exercise._

scala> val blog = Blog("MyBlog")
blog: hatena.intern.exercise.Blog = Blog(MyBlog)
```

```console
$ sbt

> testOnly hatena.intern.exercise.BlogSpec
```

