import Foundation
import UIKit

class APIService {
    static let shared = APIService()
    private let baseURL = "http://10.1.0.241:8787/v1"
    
    private init() {}
    
    // 语言代码到API参数的映射
    private let languageMapping: [String: String] = [
        "en": "English",
        "zh-Hans": "Chinese",
        "hi": "Hindi",
        "es": "Spanish",
        "fr": "French",
        "ar": "Arabic",
        "bn": "Bengali",
        "ru": "Russian",
        "pt": "Portuguese",
        "id": "Indonesian",
        "ur": "Urdu",
        "de": "German",
        "ja": "Japanese",
        "tr": "Turkish",
        "ko": "Korean",
        "vi": "Vietnamese",
        "it": "Italian",
        "th": "Thai",
        "fa": "Persian",
        "nl": "Dutch",
        "ms": "Malaysian"
    ]
    
    func fetchArticles(module: String = "index", page: Int = 1, completion: @escaping (Result<[Article], Error>) -> Void) {
        // 获取当前语言代码
        let languageCode = LanguageManager.shared.currentLanguage
        // 将语言代码转换为API需要的参数
        let languageParam = languageMapping[languageCode] ?? "English"
        
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
                let response = try JSONDecoder().decode(APIResponse<PaginatedResponse<Article>>.self, from: data)
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
                print("❌ [API] Decoding error: \(error)")
                print("📝 [API] Raw data: \(String(data: data, encoding: .utf8) ?? "")")
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
}

// API 响应的通用结构
struct APIResponse<T: Codable>: Codable {
    let status: Int
    let msg: String
    let data: T
}
