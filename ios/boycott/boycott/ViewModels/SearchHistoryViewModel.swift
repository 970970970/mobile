import Foundation

class SearchHistoryViewModel: ObservableObject {
    @Published var recentSearches: [String] = []
    
    init() {
        loadSearchHistory()
    }
    
    func loadSearchHistory() {
        recentSearches = SearchHistoryManager.shared.getSearchHistory()
    }
    
    func clearHistory() {
        SearchHistoryManager.shared.clearHistory()
        recentSearches = []
    }
} 