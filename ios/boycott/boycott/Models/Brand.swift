import Foundation

struct Brand: Codable, Identifiable {
    let id: Int
    let name: String
    let description: String?
    let status: String?
    let logoMediaId: Int?
    let logoPath: String?
    let reasons: [String]?
    let countries: [String]?
    let categories: [String]?
    let alternatives: [String]?
    let stakeholders: [Stakeholder]?
    
    // 添加 CodingKeys 来处理 snake_case 到 camelCase 的转换
    enum CodingKeys: String, CodingKey {
        case id
        case name
        case description
        case status
        case logoMediaId = "logo_media_id"
        case logoPath = "logo_path"
        case reasons
        case countries
        case categories
        case alternatives
        case stakeholders
    }
}

struct Stakeholder: Codable {
    let id: String
    let type: String
}

struct BrandListResponse: Codable {
    let total: Int
    let items: [Brand]
} 