import Foundation

struct ScanResult: Codable {
    let companyName: String
    let description: String
    let isInBoycottList: Bool
    let alternatives: [AlternativeProduct]?
}

struct AlternativeProduct: Identifiable, Codable {
    let id: Int
    let name: String
    let description: String
    let price: String
    let rating: Double
    let imageUrl: URL?
}
