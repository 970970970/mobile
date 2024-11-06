import Foundation

// 完整的文章模型
struct Article: Codable, Identifiable {
    let id: Int
    let title: String
    let content: String
    let summary: String
    let image: String?
    let language: String
    let published_at: String?
    let origin_id: Int?
    let rank: Int?
    
    // 添加计算属性来处理图片 URL
    var imageUrl: URL? {
        guard let image = image else { return nil }
        return URL(string: image)
    }
    
    // 添加计算属性来格式化日期
    var formattedDate: String {
        guard let publishedAt = published_at,
              let date = ISO8601DateFormatter().date(from: publishedAt) else {
            return ""
        }
        
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        formatter.locale = Locale(identifier: LanguageManager.shared.currentLanguage)
        
        return formatter.string(from: date)
    }
}

// 列表用的简化文章模型
struct ArticleListItem: Codable, Identifiable {
    let id: Int
    let title: String
    let summary: String
    let image: String?
    let published_at: String?
    
    // 添加相同的计算属性以保持兼容性
    var imageUrl: URL? {
        guard let image = image else { return nil }
        return URL(string: image)
    }
    
    var formattedDate: String {
        guard let publishedAt = published_at,
              let date = ISO8601DateFormatter().date(from: publishedAt) else {
            return ""
        }
        
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        formatter.locale = Locale(identifier: LanguageManager.shared.currentLanguage)
        
        return formatter.string(from: date)
    }
}

// 列表响应结构
struct ArticleListResponse: Codable {
    let total: Int
    let page: Int
    let pageSize: Int
    let list: [ArticleListItem]
}
