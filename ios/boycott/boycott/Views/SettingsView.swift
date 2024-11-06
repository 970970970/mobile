import SwiftUI

struct SettingsView: View {
    @StateObject private var languageManager = LanguageManager.shared
    @AppStorage("theme") private var isDarkMode = false
    @State private var showingLanguagePicker = false
    @State private var showingFeedbackForm = false
    @State private var showingDonationOptions = false
    @State private var showingClearCacheAlert = false
    @State private var cacheSize = "calculating".localized
    @State private var selectedLanguage: Language? = nil
    @State private var availableLanguages: [Language] = []
    
    var body: some View {
        NavigationView {
            List {
                // 语言设置
                Section(header: Text("language_settings".localized)) {
                    Button(action: { showingLanguagePicker = true }) {
                        HStack {
                            Text("current_language".localized)
                            Spacer()
                            Text(languageName(for: languageManager.currentLanguage))
                                .foregroundColor(.gray)
                        }
                    }
                }
                
                // 主题设置
                Section(header: Text("theme_settings".localized)) {
                    Toggle("dark_mode".localized, isOn: $isDarkMode)
                }
                
                // 缓存管理
                Section(header: Text("cache_management".localized)) {
                    Button(action: { showingClearCacheAlert = true }) {
                        HStack {
                            Text("clear_cache".localized)
                            Spacer()
                            Text(cacheSize)
                                .foregroundColor(.gray)
                        }
                    }
                }
                
                // 反馈与支持
                Section(header: Text("feedback_support".localized)) {
                    Button(action: { showingFeedbackForm = true }) {
                        HStack {
                            Image(systemName: "envelope")
                            Text("submit_feedback".localized)
                        }
                    }
                    
                    Link(destination: URL(string: "https://t.me/boycott_group")!) {
                        HStack {
                            Image(systemName: "message")
                            Text("community_discussion".localized)
                            Spacer()
                            Image(systemName: "arrow.up.right.square")
                                .foregroundColor(.gray)
                        }
                    }
                    
                    Button(action: { showingDonationOptions = true }) {
                        HStack {
                            Image(systemName: "heart")
                            Text("support_us".localized)
                        }
                    }
                }
                
                // 关于
                Section(header: Text("about".localized)) {
                    HStack {
                        Text("version".localized)
                        Spacer()
                        Text(Bundle.main.appVersion)
                            .foregroundColor(.gray)
                    }
                    
                    NavigationLink("privacy_policy".localized) {
                        PrivacyPolicyView()
                    }
                    
                    NavigationLink("terms_of_service".localized) {
                        UserAgreementView()
                    }
                }
            }
            .navigationTitle("settings".localized)
            .sheet(isPresented: $showingLanguagePicker) {
                LanguagePickerView(
                    selectedLanguage: $selectedLanguage,
                    languages: availableLanguages,
                    onDismiss: {
                        showingLanguagePicker = false
                        if let selected = selectedLanguage {
                            languageManager.setLanguage(selected)
                        }
                    }
                )
            }
            .sheet(isPresented: $showingFeedbackForm) {
                FeedbackFormView()
            }
            .sheet(isPresented: $showingDonationOptions) {
                DonationOptionsView()
            }
            .alert("clear_cache".localized, isPresented: $showingClearCacheAlert) {
                Button("cancel".localized, role: .cancel) { }
                Button("confirm".localized, role: .destructive) {
                    clearCache()
                }
            } message: {
                Text("clear_cache_confirm".localized)
            }
            .onAppear {
                updateCacheSize()
                // 加载可用的语言列表
                APIService.shared.fetchSupportedLanguages { result in
                    switch result {
                    case .success(let languages):
                        self.availableLanguages = languages
                        // 设置当前选中的语言
                        self.selectedLanguage = languages.first(where: { $0.code == languageManager.currentLanguage })
                    case .failure(let error):
                        print("Failed to fetch languages: \(error)")
                    }
                }
            }
        }
        .supportRTL()
    }
    
    private func languageName(for code: String) -> String {
        // 首先尝试从可用语言列表中查找
        if let language = availableLanguages.first(where: { $0.code == code }) {
            // 只返回语言名称，不包含国旗
            return language.name
        }
        
        // 如果在可用语言列表中找不到，使用备用映射
        let languageNames: [String: String] = [
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
        
        return languageNames[code] ?? code
    }
    
    private func updateCacheSize() {
        let fileManager = FileManager.default
        guard let cacheURL = fileManager.urls(for: .cachesDirectory, in: .userDomainMask).first else {
            cacheSize = "0 MB"
            return
        }
        
        DispatchQueue.global(qos: .background).async {
            do {
                let contents = try fileManager.contentsOfDirectory(at: cacheURL, includingPropertiesForKeys: [.fileSizeKey])
                let size = try contents.reduce(0) { (result, url) in
                    let resourceValues = try url.resourceValues(forKeys: [.fileSizeKey])
                    return result + (resourceValues.fileSize ?? 0)
                }
                
                DispatchQueue.main.async {
                    let formatter = ByteCountFormatter()
                    formatter.allowedUnits = [.useMB]
                    formatter.countStyle = .file
                    formatter.includesUnit = true
                    formatter.isAdaptive = false
                    
                    // 如果大小为0，直接显示"0 MB"
                    if size == 0 {
                        cacheSize = "0 MB"
                    } else {
                        cacheSize = formatter.string(fromByteCount: Int64(size))
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    cacheSize = "0 MB"
                }
            }
        }
    }
    
    private func clearCache() {
        let fileManager = FileManager.default
        guard let cacheURL = fileManager.urls(for: .cachesDirectory, in: .userDomainMask).first else { return }
        
        DispatchQueue.global(qos: .background).async {
            do {
                let contents = try fileManager.contentsOfDirectory(at: cacheURL, includingPropertiesForKeys: nil)
                for url in contents {
                    try fileManager.removeItem(at: url)
                }
                
                // 清除内存缓存
                CacheService.shared.clearAll()
                
                // 清除 URLCache
                URLCache.shared.removeAllCachedResponses()
                
                DispatchQueue.main.async {
                    updateCacheSize()
                }
            } catch {
                print("清除缓存失败: \(error)")
            }
        }
    }
}

// 扩展 Bundle 以获取应用版本号
extension Bundle {
    var appVersion: String {
        return infoDictionary?["CFBundleShortVersionString"] as? String ?? "Unknown"
    }
}

// 预览
#Preview {
    SettingsView()
}
