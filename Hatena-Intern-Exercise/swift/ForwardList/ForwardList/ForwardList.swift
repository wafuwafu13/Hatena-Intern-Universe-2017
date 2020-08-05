//
//  ForwardList.swift
//  ForwardList
//

public struct ForwardList<Element> {

    public init(array: [Element]) {
        // コンパイルを通すために実装してある
        fatalError()
    }

    public func map<T>(_ transform: (Element) throws -> T) rethrows -> [T] {
        // コンパイルを通すために実装してあるが, Sequence に準拠すれば不要になる
        fatalError()
    }

}
