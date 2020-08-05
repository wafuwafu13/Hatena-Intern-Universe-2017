開発 / ビルドツール
================================================================

- Webのフロントエンド開発におけるJS / CSS開発用のツール
  - AltJS / AltCSSのトランスパイラ
  - CSSスプライト作成
  - タスクランナー
  - Lint

Node.jsはこれらのツールを通じてWebフロントエンド開発も大幅に影響を及ぼした。


## AltJS / AltCSS

###  AltJS

- JavaScriptに変換 (transpile) される言語
  - 異常に流行ったけど最近落ち着いてきた

以下、はてなで使われたことのあるAltJS (or transpiler)

- CoffeeScript
  - 元祖AltJSだが滅びゆく運命 (さだめ)
  - ラボサービスなどで使用
  - 皆さんが見ることは無いでしょう
- TypeScript
  - 大人気AltJS
  - Mackerel, 少年ジャンプルーキーなどで使用
  - 社内では人気
- Babel
  - はてなブログで使用
  - JavaScriptの新機能を用いたコードを、今のブラウザで動作するよう変換する


### AltCSS

- CSSに変換される言語 / ツール群
  - `CSS プリプロセッサ` とも呼ばれる

以下、はてなで使われたことのあるAltCSS。

- Less
  - シンプルさ重視
  - 社内でもっとも多く使われている
- Sass
  - 高機能なAltCSS
  - ユーザーが多く、開発が活発
- PostCSS
  - プラガブルなCSS変換ツール
  - minifyなどで部分的に使われている


## タスクランナー

- 複雑なビルドやテストを実行するためのツール
  - ファイルを監視して自動ビルドしたり
  - JS / CSSを並列にビルドしたり
  - テスト実行したり

Grunt、Gulp、webpackなどが代表的

- Grunt
  - 元祖タスクランナー
  - タスク毎に中間ファイルを作成するため、重い
  - 最近あんまり見ない
- Gulp
  - Gruntに比べ、高速に動作する
  - 設定もJavaScriptで書ける
  - 開発が停滞しかけてる
- webpack
  - 厳密にはタスクランナーではなくビルドツール
  - HTML / CSS / JSを1つのファイルにまとめたりもできる

はてなでは、多くのチームでGrunt / Gulpどちらかを利用している。

## Lint

- コードの書き方をチェックしてくれるツール
  - インデント数とか
  - セミコロンつけましょうとか
  - 三項演算子使うのはやめましょうとか

最近は[ESLint](http://eslint.org/)が主流。

- どのルールもオフにすることができる
  - 自由に組み合わせることができる
  - http://eslint.org/docs/rules/
- プラガブル
  - ルールを追加するプラグインなどを簡単に書ける
  - Reactの書き方をチェックするプラグイン: https://github.com/yannickcr/eslint-plugin-react
  - 残業すると怒られるルール: https://github.com/fand/eslint-plugin-no-zangyo
