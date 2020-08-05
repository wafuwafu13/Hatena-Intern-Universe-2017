React
================================================================

<img alt="React logo" src="./react-logo.png" height="100"/>

* ビューをうまく扱うためのライブラリ
  * MVVMやFluxアーキテクチャ(後述)のView部分として使うことが多い
* データとビューをバインドして、データの変更に応じてビューを書き換えることができる
* Virtual DOMで効率的にビューを更新することができる
  * データに変更があると、Reactで管理しているビューを作り直す
  * ビューに更新があると、差分だけを実際のDOMに適用する
* はてなブログの一部で使われている
* Facebookが作ってる

なぜReactが流行っているのか
----------------------------------------------------------------

- jQuery時代の課題
  - データとビューを切り分け整理してプログラムする方法に乏しかった
  - まずい設計をしてしまうと、ビューのDOM要素のclassやdata属性や、プログラム上のオブジェクトにデータが分散し、データの一貫性が壊れたり、コードが理解しづらくなる
- Reactをつかうと
  - データとビューの関係を宣言的に記述でき、データに対応するビューが一意に決まり一貫性が保てる
  - ビューをデータと独立したコンポーネントの組み合わせとして扱うことができ、理解しやすいコードになる

Reactの使い方
----------------------------------------------------------------

- ReactではJSXというDSLでテンプレートを書く
- JSXはブラウザが直接読めないので、Babel(後述)などでプリコンパイルする必要がある
- 以下はReactでアプリケーションを書く時の雰囲気

index.html

```html
<div id="app">
</div>
```

init.js

```javascript
import ReactDOM from 'react-dom';
import App from './App';

document.addEventListener('DOMContentLoaded', () => {
  // AppはReact Component
  // ビューのパーツをComponent単位に区切って再利用できる

  ReactDOM.render(
    <App />,
    document.querySelector('#app'),
  );
});
```

App.js

```javascript
import React, { Component } from 'react';

class App extends Component {

  constructor (props) {
    super(props);
    this.state = { items: [] };
  }

  handleKeyPress (e) {
    // エンターキーが押されたら、文字列を控えて、inputから消す

    if (e.key === 'Enter') {
      this.addItem(this.textInput.value);
      this.textInput.value = '';
    }
  }

  addItem (value) {
    // setStateでstateが変更されるとrenderが実行されてビューが再描画される

    const items = [ { key: (new Date()).getTime(), value }, ...this.state.items ];
    this.setState({ items });
  }

  render () {
    // returnの中にあるHTML的なものがJSX
    // classではなくclassNameになるなど、HTMLとは若干違う
    // inputに文字を入力するとonKeyPressに書いた関数が実行される

    const items = this.state.items.map(item => <li key={item.key}>{item.value}</li>);

    return (
      <div className="App">
        <input
          ref={(input) => { this.textInput = input; }}
          onKeyPress={(e) => { this.handleKeyPress(e); }}
        />
        <ul>
          {items}
        </ul>
      </div>
    );
  }
}

export default App;
```

- Reactは、ビューのパーツをComponent単位に分割して表示する
- Componentの中で他のComponentを読み込んで利用することもできる
- Reactでレンダリングしている部分にjQueryなどで直接DOM操作を行なうと差分を追うことができなくなり壊れる
