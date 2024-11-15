import Foundation

struct HotSearchResponse: Codable {
    let status: Int
    let msg: String
    let data: [String]
} 