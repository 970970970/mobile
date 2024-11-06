import Foundation

struct Language: Codable {
    let id: Int
    let code: String
    let name: String
    let flag: String
    let status: Int
}

struct LanguageResponse: Codable {
    let items: [Language]
    let total: Int
} 