// The MIT License (MIT)
//
// Copyright (c) 2016 Hatena Co., Ltd.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.


import Foundation

protocol GitHubEndpoint: APIEndpoint {
    var path: String { get }
}

private let GitHubURL = URL(string: "https://api.github.com/")!

extension GitHubEndpoint {
    var url: URL {
        return URL(string: path, relativeTo: GitHubURL)!
    }
    var headers: Parameters? {
        return [
            "Accept": "application/vnd.github.v3+json",
        ]
    }
}

/**
 - SeeAlso: https://developer.github.com/v3/search/#search-repositories
 */
struct SearchRepositories: GitHubEndpoint {
    var path = "search/repositories"
    var query: Parameters? {
        return [
            "q"    : searchQuery,
            "page" : String(page),
        ]
    }
    typealias ResponseType = SearchResult<Repository>

    let searchQuery: String
    let page: Int
    init(searchQuery: String, page: Int) {
        self.searchQuery = searchQuery
        self.page = page
    }
}

/**
 Parse ISO 8601 format date string
 - SeeAlso: https://developer.github.com/v3/#schema
 */
private let dateFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.calendar = Calendar(identifier: Calendar.Identifier.gregorian)
    formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    return formatter
}()

struct FormattedDateConverter: JSONValueConverter {
    typealias FromType = String
    typealias ToType = Date

    fileprivate let dateFormatter: DateFormatter

    func convert(key: String, value: FromType) throws -> DateConverter.ToType {
        guard let date = dateFormatter.date(from: value) else {
            throw JSONDecodeError.unexpectedValue(key: key, value: value, message: "Invalid date format for '\(dateFormatter.dateFormat)'")
        }
        return date as DateConverter.ToType
    }
}

/**
 Search result data
 - SeeAlso: https://developer.github.com/v3/search/
 */
struct SearchResult<ItemType: JSONDecodable>: JSONDecodable {
    let totalCount: Int
    let isIncompleteResults: Bool
    let items: [ItemType]

    init(json: JSONObject) throws {
        self.totalCount = try json.get("total_count")
        self.isIncompleteResults = try json.get("incomplete_results")
        self.items = try json.get("items")
    }
}

/**
 Repository data
 - SeeAlso: https://developer.github.com/v3/search/#search-repositories
 */
struct Repository: JSONDecodable {
    let id: Int
    let name: String
    let fullName: String
    let isPrivate: Bool
    let htmlURL: URL
    let description: String?
    let isFork: Bool
    let url: URL
    let createdAt: Date
    let updatedAt: Date
    let pushedAt: Date?
    let homepage: String?
    let size: Int
    let stargazersCount: Int
    let watchersCount: Int
    let language: String?
    let forksCount: Int
    let openIssuesCount: Int
    let masterBranch: String?
    let defaultBranch: String
    let score: Double
    let owner: User

    init(json: JSONObject) throws {
        self.id = try json.get("id")
        self.name = try json.get("name")
        self.fullName = try json.get("full_name")
        self.isPrivate = try json.get("private")
        self.htmlURL = try json.get("html_url")
        self.description = try json.get("description")
        self.isFork = try json.get("fork")
        self.url = try json.get("url")
        self.createdAt = try json.get("created_at", converter: FormattedDateConverter(dateFormatter: dateFormatter))
        self.updatedAt = try json.get("updated_at", converter: FormattedDateConverter(dateFormatter: dateFormatter))
        self.pushedAt = try json.get("pushed_at", converter: FormattedDateConverter(dateFormatter: dateFormatter))
        self.homepage = try json.get("homepage")
        self.size = try json.get("size")
        self.stargazersCount = try json.get("stargazers_count")
        self.watchersCount = try json.get("watchers_count")
        self.language = try json.get("language")
        self.forksCount = try json.get("forks_count")
        self.openIssuesCount = try json.get("open_issues_count")
        self.masterBranch = try json.get("master_branch")
        self.defaultBranch = try json.get("default_branch")
        self.score = try json.get("score")
        self.owner = try json.get("owner")
    }
}

/**
 User data
 - SeeAlso: https://developer.github.com/v3/search/#search-repositories
 */
struct User: JSONDecodable {
    let login: String
    let id: Int
    let avatarURL: URL
    let gravatarID: String
    let url: URL
    let receivedEventsURL: URL
    let type: String

    init(json: JSONObject) throws {
        self.login = try json.get("login")
        self.id = try json.get("id")
        self.avatarURL = try json.get("avatar_url")
        self.gravatarID = try json.get("gravatar_id")
        self.url = try json.get("url")
        self.receivedEventsURL = try json.get("received_events_url")
        self.type = try json.get("type")
    }
}
