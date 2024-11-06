import SwiftUI

extension String {
    var localized: String {
        let languageCode = LanguageManager.shared.currentLanguage
        
        // 获取对应语言的 Bundle
        guard let bundlePath = Bundle.main.path(forResource: languageCode, ofType: "lproj"),
              let bundle = Bundle(path: bundlePath) else {
            print("⚠️ [Localization] Failed to find bundle for language: \(languageCode)")
            return NSLocalizedString(self, comment: "")
        }
        
        let localizedString = bundle.localizedString(forKey: self, value: nil, table: "Localizable")
        print("🔤 [Localization] Key: \(self), Language: \(languageCode), Result: \(localizedString)")
        return localizedString
    }
}

extension View {
    func supportRTL() -> some View {
        let isRTL = LanguageManager.shared.isRTL
        print("↔️ [RTL] Setting layout direction: \(isRTL ? "RTL" : "LTR")")
        return self.environment(\.layoutDirection, isRTL ? .rightToLeft : .leftToRight)
    }
} 