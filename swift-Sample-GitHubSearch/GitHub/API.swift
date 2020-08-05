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

enum APIError: Error {
    case emptyBody
    case unexpectedResponseType
}

enum HTTPMethod: String {
    case OPTIONS
    case GET
    case HEAD
    case POST
    case PUT
    case DELETE
    case TRACE
    case CONNECT
}

protocol APIEndpoint {
    var url: URL { get }
    var method: HTTPMethod { get }
    var query: Parameters? { get }
    var headers: Parameters? { get }
    associatedtype ResponseType: JSONDecodable
}

extension APIEndpoint {
    var method: HTTPMethod {
        return .GET
    }
    var query: Parameters? {
        return nil
    }
    var headers: Parameters? {
        return nil
    }
}

extension APIEndpoint {
    private var urlRequest: URLRequest {
        var components = URLComponents(url: url, resolvingAgainstBaseURL: true)
        components?.queryItems = query?.parameters.map(URLQueryItem.init)
        var req = URLRequest(url: components?.url ?? url)
        req.httpMethod = method.rawValue
        for case let (key, value?) in headers?.parameters ?? [:] {
            req.addValue(value, forHTTPHeaderField: key)
        }
        return req
    }

    func request(_ session: URLSession, callback: @escaping (APIResult<ResponseType>) -> Void) -> URLSessionDataTask {
        let task = session.dataTask(with: urlRequest) { (data, response, error) in
            if let e = error {
                callback(.failure(e))
            } else if let data = data {
                do {
                    guard let dic = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] else {
                        throw APIError.unexpectedResponseType
                    }
                    let response = try ResponseType(json: JSONObject(json: dic))
                    callback(.success(response))
                } catch {
                    callback(.failure(error))
                }
            } else {
                callback(.failure(APIError.emptyBody))
            }
        }
        task.resume()
        return task
    }
}

enum APIResult<Response> {
    case success(Response)
    case failure(Error)
}

struct Parameters: ExpressibleByDictionaryLiteral {
    typealias Key = String
    typealias Value = String?
    private(set) var parameters: [Key: Value] = [:]

    init(dictionaryLiteral elements: (Parameters.Key, Parameters.Value)...) {
        for case let (key, value?) in elements {
            parameters[key] = value
        }
    }
}
