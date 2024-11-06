struct PaginatedResponse<T: Codable>: Codable {
    let total: Int
    let page: Int
    let pageSize: Int
    let list: [T]
} 