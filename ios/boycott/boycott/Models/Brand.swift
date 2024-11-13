import Foundation

struct Brand: Codable, Identifiable {
    let id: Int
    let name: String
    let description: String
    let status: String
    let logoMediaId: Int?
    let logoPath: String?
    
    enum CodingKeys: String, CodingKey {
        case id
        case name
        case description
        case status
        case logoMediaId = "logo_media_id"
        case logoPath = "logo_path"
    }
}

struct BrandListResponse: Codable {
    let status: Int
    let msg: String
    let data: BrandListData
}

struct BrandListData: Codable {
    let items: [Brand]
    let total: Int
} 