# JavaScript 課題

この課題では、JavaScript の基礎と簡単な DOM 操作の理解を確認します。
DOM 操作とは、HTML 文書の構造などをプログラムで変更する処理のことです。
web 開発においては主にユーザーインターフェイスの動的な変更のために使用されます。

## 使用するブラウザ

JavaScript には [Node.js](http://nodejs.org/) などブラウザ以外の処理系も存在しますが、本テキストではブラウザの処理系のみを対象とします。
課題やテストの実行には [Firefox](http://www.mozilla.jp/firefox/) または [Google Chrome](http://www.google.co.jp/intl/ja/chrome/browser/) の最新バージョンを使用してください。
クロスブラウザでの挙動について考慮する必要はありません。

### 開発者ツール

ブラウザに付属する開発者用ツールを使ってみてください。
ブラウザの開発者用ツールには、次のような機能があります。

* ページの HTML 文書構造を動的に表示する
* JavaScript コンソールで JavaScript 実行時のエラーを確認する
* デバッガで JavaScript の処理をステップ実行する
  * その際、スコープ内の変数の値を確認できる
* イベントの発火に対してブレークポイントを設定する

これらのツールは完璧に使いこなす必要はありませんが、JavaScript のエラーやログの確認方法を知っておくと、課題を進めやすくなるでしょう。

Firefox, Google Chrome の開発用ツールについて参考リンクを紹介します。

* Firefox
  * Web コンソール: [Web コンソール - Tools | MDN](https://developer.mozilla.org/ja/docs/Tools/Web_Console)
  * デバッガ: [デバッガ - Tools | MDN](https://developer.mozilla.org/ja/docs/Tools/Debugger)
* Google Chrome
  * ドキュメント: [Chrome DevTools — Google Developers](https://developers.google.com/chrome-developer-tools/)


## 基礎編

課題 JS-1 から JS-3 では、JavaScript を使って Web サーバーのアクセスログ (を模した文字列) を解析し、HTML ドキュメント上にアクセスログの表を表示する、という処理を実装していきます。
これらの課題の解答には、外部ライブラリを使用しないようにしてください。

### 課題 JS-1

Web サーバーへのアクセスログを表す LTSV 形式の文字列を引数として受け取り、その文字列をパースしてオブジェクトの配列を返す関数 `parseLTSVLog` を作成してください。
`parseLTSVLog` は `Hatena-Intern-Exercise/js/main.js` ファイルにグローバル関数として記述してください。

アクセスログを表す LTSV 形式の文字列の例を次に示します。
タブ文字は空白として表示されるので注意してください。

```
path:/	epoch:200000
path:/bookmark	epoch:123456
```

1 行が 1 つのリクエストに対応しています。
1 行を 1 つの JavaScript のオブジェクトにし、アクセスログ中のラベルと値をプロパティとして持たせるようにしてください。
基本的に、アクセスログ中の値は文字列としてプロパティの値にすれば良いですが、`epoch` というラベルの値についてのみは数値にしてください。

すなわち、

```javascript
const logStr =
    'path:/\tepoch:200000\n' +
    'path:/help\tepoch:300000\n' +
    'path:/\tepoch:250000\n';
```

という文字列を引数として受け取り、

```javascript
[
    { path: '/',     epoch: 200000 },
    { path: '/help', epoch: 300000 },
    { path: '/',     epoch: 250000 },
]
```

という構造の配列を返すように `parseLTSVLog` 関数を実装してください。
以下、このような形式のデータを `ログデータ配列` と呼びます。

実際のアクセスログには様々なデータが含まれますが、課題では簡単のため `path` 及び `epoch` というラベルのみ考慮すればよいものとします。
`path` というラベルの値には、URL として有効な文字列が与えられます。
`epoch` というラベルの値としては、整数値とみなせる文字列が与えられます。

課題で指定されていない仕様は、自由に決めてよいものとします。
例えば、引数が与えられずに関数が呼び出された場合は、例外を送出しても良いですし、空の配列を返すようにしても構いません。

`Hatena-Intern-Exercise/js/test.js` には `parseLTSVLog` のテストが予め記述されています。
ブラウザで `Hatena-Intern-Exercise/js/test.html` を表示すると、自動的にテストが実行されるようになっています。
最低限、最初から書かれているテストに通過するように `parseLTSVLog` 関数を実装してください。

テストには [Mocha](https://mochajs.org/) 及び [chai](http://chaijs.com/) を利用しています。
余裕があれば、これらのドキュメントを参照し、テストを追加してみてください (例: 予期せぬ値が引数として渡された場合に関するテスト)。


### 課題 JS-2

`div` 要素を表すDOMオブジェクト、及びログデータ配列を引数として受け取り、受け取った `div` 要素の直下にログデータの表を生成する関数 `createLogTable` を作成してください。
`createLogTable` は `Hatena-Intern-Exercise/js/main.js` ファイルにグローバル関数として記述してください。

`createLogTable` の具体的な呼び出し方は次のようになります。

```javascript
const containerElem = document.getElementById('table-container');
createLogTable(containerElem, [
    { path: '/',     epoch: 200000 },
    { path: '/help', epoch: 300000 },
    { path: '/',     epoch: 250000 },
]);
```

この時、 `containerElem` 直下に次のような `table` 要素が生成されるようにしてください。

```html
<table>
  <thead><tr><th>path</th><th>epoch</th></tr></thead>
  <tbody>
    <tr><td>/</td><td>200000</td></tr>
    <tr><td>/help</td><td>300000</td></tr>
    <tr><td>/</td><td>250000</td></tr>
  </tbody>
</table>
```

`tbody` 要素内の `tr` 要素は、引数として受け取ったログデータ配列の各要素に対応します。
ログデータ配列の要素が表の上から順番に並ぶようにしてください。
表は 2 列とし、最初の列には `path` の値を、次の列には `epoch` の値を表示するものとします。

課題 JS-1 と同じく、この関数のテストも `Hatena-Intern-Exercise/js/test.js` に記述されています。
最低限、最初から書かれているテストを通過するように実装してください。
余裕があれば新たにテストを追加してみましょう。

参考になりそうな MDN (Mozilla Developer Network) のページを以下に挙げておきます。
ここで紹介しているメソッド以外を使用しても構いません。

* ノードに子ノードを追加: [Node.appendChild - Web API リファレンス | MDN](https://developer.mozilla.org/ja/docs/Web/API/Node.appendChild)
* Element ノードの生成: [document.createElement - Web API リファレンス | MDN](https://developer.mozilla.org/ja/docs/Web/API/document.createElement)
* ノードの子孫のテキストを設定したり読み取ったりする: [Node.textContent - Web API リファレンス | MDN](https://developer.mozilla.org/ja/docs/Web/API/Node.textContent)


### 課題 JS-3

`Hatena-Intern-Exercise/js/js-3.html` のページが次のように動作するよう、 `Hatena-Intern-Exercise/js/js-3.js` に JavaScript の処理を記述してください。

* ユーザーが `textarea` 要素に LTSV 形式のアクセスログを入力し「表に出力する」 ボタンをクリックすると、`access-log-table-container` という `id` 属性をもつ要素の直下に、アクセスログの表を表す `table` 要素が作られる。

この課題では、課題 JS-1 と JS-2 で作成した `parseLTSVLog` `createLogTable` を利用してください。
これらの関数がグローバル関数として正しく実装されていれば、 `Hatena-Intern-Exercise/js/js-3.js` からは `parseLTSVLog(logData)` のように呼び出すことができます。

参考になりそうな MDN のページを以下に挙げておきます。
ここで紹介しているメソッド以外を使用しても構いません。

* id を指定して文書中の Element ノードを取得: [document.getElementById - Web API リファレンス | MDN](https://developer.mozilla.org/ja/docs/Web/API/document.getElementById)
* イベントリスナを追加: [EventTarget.addEventListener - Web API リファレンス | MDN](https://developer.mozilla.org/ja/docs/Web/API/EventTarget.addEventListener)


## 応用編

### 課題 JS-4

課題 JS-3 で制作したページに、ログの検索機能を実装してください。

どのような検索機能を実装するか、どのような UI を作るかは全て自由とします。
複数の機能を作っても構いません。
例えば、以下のような機能が考えられます。

- `path` を検索できる機能
  - `bookmark` と入力して「検索」ボタンを押すと、`path` の値に `bookmark` が含まれるログが表示される、など
- `POST` メソッドのログのみ表示する機能
- 2つ以上の条件を組み合わせて検索できる機能
- インクリメンタルサーチ機能

この課題では好きな JavaScript ライブラリを使って構いません。
ただし検索機能は自分で実装するようにして下さい (検索機能を提供するライブラリの利用は禁止します)。

この課題のための HTML ファイルや JS ファイルは新たに作成してください。
ファイル名は特に指定しませんが、 `Hatena-Intern-Exercise/js/js-4.html` や `js/js-4.js` など、わかりやすい名前にしてください。

なお、この課題で使うログデータは `Hatena-Intern-Exercise/sample_data/log.ltsv` (または `large_log.ltsv`) にあるデータを `<textarea>` に入れて利用するようにしてください。
実行の度に貼り付けても良いし、予めDOMに埋め込んでおいても構いません。
