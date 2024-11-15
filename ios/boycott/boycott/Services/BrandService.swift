import Foundation

class BrandService {
    static let shared = BrandService()
    private let apiService = APIService.shared
    
    func fetchBrands(keyword: String = "", page: Int = 1, pageSize: Int = 20, completion: @escaping (Result<BrandListResponse, Error>) -> Void) {
        let url = URL(string: "\(apiService.baseURL)/brands/list?keywords=\(keyword)&limit=\(pageSize)&offset=\((page - 1) * pageSize)")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        print("🔍 [BrandService] Fetching brands with URL: \(url.absoluteString)")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ [BrandService] Network error: \(error.localizedDescription)")
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
                return
            }
            
            guard let data = data else {
                print("❌ [BrandService] No data received")
                DispatchQueue.main.async {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                }
                return
            }
            
            do {
                let response = try JSONDecoder().decode(APIResponse<BrandListResponse>.self, from: data)
                DispatchQueue.main.async {
                    if response.status == 0 {
                        completion(.success(response.data))
                    } else {
                        completion(.failure(NSError(domain: "", code: response.status, userInfo: [NSLocalizedDescriptionKey: response.msg])))
                    }
                }
            } catch {
                print("❌ [BrandService] Decoding error:", error)
                print("📝 [BrandService] Raw data:", String(data: data, encoding: .utf8) ?? "")
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
} 
