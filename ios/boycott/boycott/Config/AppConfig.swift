import Foundation

enum AppConfig {
    static let apiHost = "http://10.1.0.241:8787/v1"
    static let mediaHost = "http://10.1.0.241:8787/v1/media/local"
    
    #if DEBUG
    static let isDebug = true
    #else
    static let isDebug = false
    #endif
} 