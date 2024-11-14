import SwiftUI

class LanguageManager: ObservableObject {
    static let shared = LanguageManager()
    static let languageChangedNotification = Notification.Name("com.boycott.languageChanged")
    
    @Published private(set) var currentLanguage: String = {
        let savedLanguage = UserDefaults.standard.string(forKey: "selectedLanguage")
        let systemLanguage = Locale.current.language.languageCode?.identifier ?? "en"
        let initialLanguage = savedLanguage ?? systemLanguage
        print("🚀 [LanguageManager] Initializing with language: \(initialLanguage)")
        return initialLanguage
    }() {
        didSet {
            print("🔄 [LanguageManager] Language changing from \(oldValue) to \(currentLanguage)")
            UserDefaults.standard.set(currentLanguage, forKey: "selectedLanguage")
            UserDefaults.standard.synchronize()
            
            NotificationCenter.default.post(
                name: LanguageManager.languageChangedNotification,
                object: nil
            )
        }
    }
    
    // 添加语言变更标记
    @Published private(set) var languageChanged = false
    
    private init() {
        // 初始化时不需要额外的设置，因为属性已经在声明时初始化
        print("🚀 [LanguageManager] Manager initialized")
    }
    
    func setLanguage(_ language: Language) {
        print("🌍 [LanguageManager] Setting language to: \(language.code)")
        let oldLanguage = currentLanguage
        let newLanguage = normalizeLanguageCode(language.code)
        
        if oldLanguage != newLanguage {
            currentLanguage = newLanguage
            languageChanged = true
            print("🔄 [LanguageManager] Language changed: \(oldLanguage) -> \(newLanguage)")
        }
    }
    
    // 添加重置变更标记的方法
    func resetLanguageChanged() {
        languageChanged = false
        print("🔄 [LanguageManager] Reset language changed flag")
    }
    
    private func normalizeLanguageCode(_ code: String) -> String {
        let components = code.split(separator: "-")
        let baseCode = String(components[0])
        
        switch baseCode {
        case "zh": return "zh-Hans"  // 简体中文
        case "ar", "fa", "ur": return baseCode  // 从右到左的语言
        case "en": return "en"
        case "hi": return "hi"
        case "es": return "es"
        case "fr": return "fr"
        case "bn": return "bn"
        case "ru": return "ru"
        case "pt": return "pt"
        case "id": return "id"
        case "de": return "de"
        case "ja": return "ja"
        case "tr": return "tr"
        case "ko": return "ko"
        case "vi": return "vi"
        case "it": return "it"
        case "th": return "th"
        case "nl": return "nl"
        case "ms": return "ms"
        default: return "en"
        }
    }
    
    var locale: Locale {
        print("🌐 [LanguageManager] Getting locale for: \(currentLanguage)")
        return Locale(identifier: currentLanguage)
    }
    
    var isRTL: Bool {
        // 阿拉伯语、波斯语、乌尔都语是从右到左的语言
        let rtlLanguages = ["ar", "fa", "ur"]
        let baseCode = currentLanguage.split(separator: "-").first.map(String.init) ?? ""
        return rtlLanguages.contains(baseCode)
    }
} 