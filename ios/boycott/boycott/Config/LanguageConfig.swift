import Foundation

struct LanguageConfig {
    // 语言代码到API参数的映射
    static let apiMapping: [String: String] = [
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
    
    // 语言代码规范化映射
    static let normalizedMapping: [String: String] = [
        "zh": "zh-Hans",  // 简体中文
        "ar": "ar",       // 阿拉伯语
        "fa": "fa",       // 波斯语
        "ur": "ur",       // 乌尔都语
        "en": "en",
        "hi": "hi",
        "es": "es",
        "fr": "fr",
        "bn": "bn",
        "ru": "ru",
        "pt": "pt",
        "id": "id",
        "de": "de",
        "ja": "ja",
        "tr": "tr",
        "ko": "ko",
        "vi": "vi",
        "it": "it",
        "th": "th",
        "nl": "nl",
        "ms": "ms"
    ]
    
    // RTL语言列表
    static let rtlLanguages: Set<String> = ["ar", "fa", "ur"]
    
    // 获取规范化的语言代码
    static func normalizeLanguageCode(_ code: String) -> String {
        let baseCode = String(code.split(separator: "-")[0])
        return normalizedMapping[baseCode] ?? "en"
    }
    
    // 检查是否是RTL语言
    static func isRTL(_ languageCode: String) -> Bool {
        let baseCode = String(languageCode.split(separator: "-")[0])
        return rtlLanguages.contains(baseCode)
    }
    
    // 获取API参数对应的语言名称
    static func getAPILanguageName(_ languageCode: String) -> String {
        return apiMapping[languageCode] ?? "English"
    }
}
