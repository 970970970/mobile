import Foundation

class SearchHistoryViewModel: ObservableObject {
    @Published var recentSearches: [String] = []
    private let userDefaults = UserDefaults.standard
    private let searchHistoryKey = "recentSearches"
    private let maxHistoryItems = 10
    
    init() {
        loadSearchHistory()
    }
    
    private func loadSearchHistory() {
        recentSearches = userDefaults.stringArray(forKey: searchHistoryKey) ?? []
    }
    
    func addSearch(_ query: String) {
        var searches = recentSearches
        searches.removeAll { $0 == query }
        searches.insert(query, at: 0)
        if searches.count > maxHistoryItems {
            searches = Array(searches.prefix(maxHistoryItems))
        }
        recentSearches = searches
        saveSearchHistory()
    }
    
    func clearHistory() {
        recentSearches = []
        saveSearchHistory()
    }
    
    private func saveSearchHistory() {
        userDefaults.set(recentSearches, forKey: searchHistoryKey)
    }
} 