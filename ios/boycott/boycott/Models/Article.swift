import Foundation

struct Article: Identifiable, Codable {
    let id: Int
    let title: String
    let summary: String
    let image: String?
    let published_at: String
    
    var imageUrl: URL? {
        guard let image = image else { return nil }
        return URL(string: image)
    }
    
    var formattedDate: String {
        return published_at
    }
}
