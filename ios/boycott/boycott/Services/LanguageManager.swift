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
        return LanguageConfig.normalizeLanguageCode(code)
    }
    
    var locale: Locale {
        print("🌐 [LanguageManager] Getting locale for: \(currentLanguage)")
        return Locale(identifier: currentLanguage)
    }
    
    var isRTL: Bool {
        return LanguageConfig.isRTL(currentLanguage)
    }
} 