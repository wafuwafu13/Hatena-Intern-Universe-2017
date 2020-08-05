その他の方法
================================================================

## ライブラリ使う

- jQuery
- axios
- request
- superagent
- etc...

jQueryについては後ほど解説します。


## fetch

- XMLHttpRequestはなし崩し的に使われてきた
- WHATWGが新しい仕様を策定中
  - PromiseベースのAPI
  - `Headers` `Request` `Response` といったクラスを定義
  - 最近のブラウザだと使える
    - 2017-07-19現在では、ほとんどのブラウザで対応(IE11は非対応)
- 参考
  - [WHATWGによる仕様](https://fetch.spec.whatwg.org/)
  - [Fetch API 解説、または Web において "Fetch する" とは何か？ - Block Rockin’ Codes](http://jxck.hatenablog.com/entry/whatwg-fetch)


## fetchの使用例

```javascript
fetch(url)
    .then(res => res.text())    // .json() や .blob() もある
    .then(r => console.log(r));

// いろいろオプション書いたり
const headers = new Header();
fetch(url, {
    method  : 'POST',
    headers : headers,
    cache   : 'no-cache',
});

// 途中でキャンセルできる
const fetching = fetch(url);
abortButton.addEventListener('click', () => {
    fetching.then(res => res.body.cancel());
});
```
