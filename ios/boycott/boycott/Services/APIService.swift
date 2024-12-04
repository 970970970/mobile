import Foundation
import UIKit

class APIService {
    static let shared = APIService()
    let baseURL = AppConfig.apiHost
    
    // 添加一个公共的 getter 方法
    var apiBaseURL: String {
        return baseURL
    }
    
    private init() {}
    
    // 语言代码到API参数的映射
    private var languageMapping: [String: String] {
        return LanguageConfig.apiMapping
    }
    
    func fetchArticles(module: String = "index", page: Int = 1, completion: @escaping (Result<[ArticleListItem], Error>) -> Void) {
        // 获取当前语言代码
        let languageCode = LanguageManager.shared.currentLanguage
        // 将语言代码转换为API需要的参数
        let languageParam = LanguageConfig.getAPILanguageName(languageCode)
        
        print("🌐 [API] Fetching articles for language: \(languageParam) (code: \(languageCode))")
        
        let url = URL(string: "\(baseURL)/articles/list/\(module)/\(languageParam)?page=\(page)")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        print("🔗 [API] Request URL: \(url.absoluteString)")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ [API] Network error: \(error.localizedDescription)")
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
                return
            }
            
            guard let data = data else {
                print("❌ [API] No data received")
                DispatchQueue.main.async {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                }
                return
            }
            
            do {
                let response = try JSONDecoder().decode(APIResponse<ArticleListResponse>.self, from: data)
                print("✅ [API] Successfully fetched \(response.data.list.count) articles")
                DispatchQueue.main.async {
                    if response.status == 0 {
                        completion(.success(response.data.list))
                    } else {
                        print("⚠️ [API] Error status: \(response.status), message: \(response.msg)")
                        completion(.failure(NSError(domain: "", code: response.status, userInfo: [NSLocalizedDescriptionKey: response.msg])))
                    }
                }
            } catch {
                print("❌ [API] Decoding error:", error)
                print("📝 [API] Raw data:", String(data: data, encoding: .utf8) ?? "")
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func searchCompany(name: String, completion: @escaping (Result<ScanResult, Error>) -> Void) {
        let url = URL(string: "\(baseURL)/companies/search")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body = ["name": name]
        request.httpBody = try? JSONEncoder().encode(body)
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
                return
            }
            
            guard let data = data else {
                DispatchQueue.main.async {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                }
                return
            }
            
            do {
                let response = try JSONDecoder().decode(APIResponse<ScanResult>.self, from: data)
                DispatchQueue.main.async {
                    completion(.success(response.data))
                }
            } catch {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func recognizeImage(_ image: UIImage, completion: @escaping (Result<ScanResult, Error>) -> Void) {
        let url = URL(string: "\(baseURL)/recognize")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        // 将图片转换为JPEG数据
        guard let imageData = image.jpegData(compressionQuality: 0.8) else {
            completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to convert image to data"])))
            return
        }
        
        // 创建multipart/form-data请求
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        var body = Data()
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
        body.append(imageData)
        body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)
        
        request.httpBody = body
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
                return
            }
            
            guard let data = data else {
                DispatchQueue.main.async {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                }
                return
            }
            
            do {
                let response = try JSONDecoder().decode(APIResponse<ScanResult>.self, from: data)
                DispatchQueue.main.async {
                    completion(.success(response.data))
                }
            } catch {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func fetchSupportedLanguages(completion: @escaping (Result<[Language], Error>) -> Void) {
        let url = URL(string: "\(baseURL)/languages?limit=100")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
                return
            }
            
            guard let data = data else {
                DispatchQueue.main.async {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                }
                return
            }
            
            do {
                let response = try JSONDecoder().decode(APIResponse<LanguageResponse>.self, from: data)
                DispatchQueue.main.async {
                    if response.status == 0 {
                        completion(.success(response.data.items))
                    } else {
                        completion(.failure(NSError(domain: "", code: response.status, userInfo: [NSLocalizedDescriptionKey: response.msg])))
                    }
                }
            } catch {
                print("Decoding error:", error)
                print("Raw data:", String(data: data, encoding: .utf8) ?? "")
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    // 添加新的方法来获取用户协议和隐私政策
    func fetchAgreement(type: String, completion: @escaping (Result<Article, Error>) -> Void) {
        // 获取当前语言代码
        let languageCode = LanguageManager.shared.currentLanguage
        // 将语言代码转换为API需要的参数
        let languageParam = LanguageConfig.getAPILanguageName(languageCode)
        
        let url = URL(string: "\(baseURL)/articles/mod/\(type)/\(languageParam)")!
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
                return
            }
            
            guard let data = data else {
                DispatchQueue.main.async {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                }
                return
            }
            
            do {
                let response = try JSONDecoder().decode(APIResponse<Article>.self, from: data)
                if response.status == 0 {
                    DispatchQueue.main.async {
                        completion(.success(response.data))
                    }
                } else {
                    DispatchQueue.main.async {
                        completion(.failure(NSError(domain: "", code: response.status, userInfo: [NSLocalizedDescriptionKey: response.msg])))
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    // 便捷方法
    func fetchUserAgreement(completion: @escaping (Result<Article, Error>) -> Void) {
        fetchAgreement(type: "user_agreement", completion: completion)
    }
    
    func fetchPrivacyPolicy(completion: @escaping (Result<Article, Error>) -> Void) {
        fetchAgreement(type: "privacy_policy", completion: completion)
    }
    
    // 添加获取单篇文章的方法
    func fetchArticle(id: Int, completion: @escaping (Result<Article, Error>) -> Void) {
        let url = URL(string: "\(baseURL)/articles/\(id)")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        print("🌐 [API] Fetching article with ID: \(id)")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ [API] Network error: \(error.localizedDescription)")
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
                return
            }
            
            guard let data = data else {
                print("❌ [API] No data received")
                DispatchQueue.main.async {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                }
                return
            }
            
            do {
                let response = try JSONDecoder().decode(APIResponse<Article>.self, from: data)
                print("✅ [API] Successfully fetched article")
                DispatchQueue.main.async {
                    if response.status == 0 {
                        completion(.success(response.data))
                    } else {
                        print("⚠️ [API] Error status: \(response.status), message: \(response.msg)")
                        completion(.failure(NSError(domain: "", code: response.status, userInfo: [NSLocalizedDescriptionKey: response.msg])))
                    }
                }
            } catch {
                print("❌ [API] Decoding error:", error)
                print("📝 [API] Raw data:", String(data: data, encoding: .utf8) ?? "")
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func fetchHotSearches(completion: @escaping (Result<[String], Error>) -> Void) {
        let endpoint = "\(baseURL)/brands/hot-searches"
        
        URLSession.shared.dataTask(with: URL(string: endpoint)!) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            guard let data = data else {
                completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                return
            }
            
            do {
                let response = try JSONDecoder().decode(APIResponse<[String]>.self, from: data)
                DispatchQueue.main.async {
                    if response.status == 0 {
                        completion(.success(response.data))
                    } else {
                        completion(.failure(NSError(domain: "", code: response.status, userInfo: [NSLocalizedDescriptionKey: response.msg])))
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func searchBrands(keyword: String, completion: @escaping (Result<BrandListResponse, Error>) -> Void) {
        let encodedKeyword = keyword.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        let endpoint = "\(baseURL)/brands/list?keywords=\(encodedKeyword)&limit=20&offset=0"
        
        URLSession.shared.dataTask(with: URL(string: endpoint)!) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
                return
            }
            
            guard let data = data else {
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
                DispatchQueue.main.async {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
}

// API 响应的通用结构
struct APIResponse<T: Codable>: Codable {
    let status: Int
    let msg: String
    let data: T
}
