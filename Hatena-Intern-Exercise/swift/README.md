# 事前課題

インターン参加前に以下の課題を課します。

1. はてな教科書の[プログラミング言語 Swift](/Hatena-Textbook/swift-programming-language.md) を一通り読み、Swift について学ぶ
2. 学んだ知識を活かして、Swift で単方向リストを実装する
3. （オプション）単方向リストに任意の機能を加える

これらの課題を通して、プログラミング言語としての Swift への理解を深めてください。

## 1. はてな教科書のプログラミング言語 Swift を一通り読み、Swift について学ぶ

[プログラミング言語 Swift](/Hatena-Textbook/swift-programming-language.md)

まずは Swift について一通り学びます。

余力がある場合には Apple の [The Swift Programming Language](https://developer.apple.com/library/content/documentation/Swift/Conceptual/Swift_Programming_Language/) を読むと、さらに網羅的に Swift を知ることができます。

## 2. 学んだ知識を活かして、Swift で単方向リストを実装する

それでは実際に手を動かしてみましょう。

ここでは単方向リストを実装してみます。単方向リストは連結リストの一種で、要素の挿入や削除が定数時間で実行可能であり、このような操作を行う場合に連続的なメモリ領域を使用する配列と較べてパフォーマンス上有利になることが多いという特性を持っています（一方でその他の操作では通常の配列の方が一般に高速です）。

単方向リストでは、値を保持する各ノードが、自身の次のノードへの参照を持つ、というデータ構造になります。このような構造を作成して、単方向リストを実装してください。

### 条件

単方向リストの型を `ForwardList<Element>` としたとき、以下の条件を満たすこと。

1. `ForwardList<Element>` は `Array<Element>` で初期化できること（イニシャライザ `init(array: Array<Element>)` を持つこと)
2. `ForwardList<Element>` は Swift 標準ライブラリの `Sequence` protocol に準拠していること

これらの条件を満たすとき、以下の XCTest のテストが通るでしょう。

```swift
import XCTest
import ForwardList

class ForwardListTests: XCTestCase {

    func test_map() {
        let list: ForwardList<Int> = ForwardList(array: [1, 2, 3])
        XCTAssertEqual(list.map({ $0 }), [1, 2, 3])
    }

}
```

### ヒント

Swift の `Sequence` は、これに準拠すると `for...in` で利用できるようになります（[参考](/Hatena-Textbook/swift-programming-language.md#for-in)）。また protocol extension により、`map` などの高階関数や（要素が比較可能であれば）`sorted` など、自動的に様々な機能が利用可能になります。

`Sequence` を実装するには、`IteratorProtocol` protocol を実装した何らかのインスタンスを `makeIterator()` メソッドで返す必要があります。`IteratorProtocol` は `next()` メソッドを持つオブジェクトで、デザインパターンにおける Iterator に相当するものです。

以下に `Sequence` を実装する簡単な例を紹介します。課題の参考にしてください。

```swift
struct AnimalIterator<Animal>: IteratorProtocol {
    typealias Element = Animal

    let animals: [Animal]
    var index: Int = 0

    init(animals: [Animal]) {
        self.animals = animals
    }

    mutating func next() -> AnimalIterator.Element? {
        guard animals.count > index else {
            return nil
        }
        let element = animals[index]
        index += 1
        return element
    }
}

struct Farm<Animal>: Sequence {
    typealias Iterator = AnimalIterator<Animal>

    let animals: [Animal]

    func makeIterator() -> Farm.Iterator {
        return AnimalIterator(animals: animals)
    }
}

struct Cow {
    let name: String
}

let farm = Farm(animals: [Cow(name: "John"), Cow(name: "Paul")])
for cow in farm {
    print(cow.name)
}
```

この例では、実際には `Farm` の内部に保持する配列の要素を順に返しているだけです。`AnimalIterator` は配列のインデックスを利用してランダムアクセスしていますが、連結リストではこのようにはいかないことでしょう。ぜひ工夫して実装してください。

## 3. （オプション）単方向リストに任意の機能を加える

Swift を学んだあなたにとって、ここまでの課題は少し物足りなかったかもしれません。ここからは、自分で実装した単方向リストに、単方向リストがより便利になるような機能を好きなだけ付加してみましょう。また追加した機能が正常に動作していることを確かめるテストを書いてみましょう。

### 例

以下に機能の例を示します。

- 要素の挿入や削除の機能をつける
- 双方向リストにする
- Swift の他の protocol に準拠させる
	- `ExpressibleByArrayLiteral`
	- `Collection`
- 単方向リストを `+` 演算子で結合できるようにする

---

課題2と課題3の回答用に、このリポジトリの Xcode プロジェクトファイルを利用できます。`ForwardList/ForwardList.swift` を実装し、`ForwardListTests/ForwardListTests.swift` でテストできます。
