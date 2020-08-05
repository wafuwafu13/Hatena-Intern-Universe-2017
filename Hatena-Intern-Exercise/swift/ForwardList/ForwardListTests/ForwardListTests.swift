//
//  ForwardListTests.swift
//  ForwardListTests
//

import XCTest
import ForwardList


class ForwardListTests: XCTestCase {

    func test_map() {
        let list: ForwardList<Int> = ForwardList(array: [1, 2, 3])
        XCTAssertEqual(list.map({ $0 }), [1, 2, 3])
    }
    
}
