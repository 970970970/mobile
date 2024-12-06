import Foundation

class SearchHistoryViewModel: ObservableObject {
    @Published var recentSearches: [String] = []
    
    init() {
        loadSearchHistory()
    }
    
    func loadSearchHistory() {
        recentSearches = SearchHistoryManager.shared.getSearchHistory()
    }
    
    func addSearch(_ query: String) {
        SearchHistoryManager.shared.addSearch(query)
        loadSearchHistory()
    }
    
    func deleteSearchHistory(at offsets: IndexSet) {
        offsets.forEach { index in
            if index < recentSearches.count {
                SearchHistoryManager.shared.removeSearch(recentSearches[index])
            }
        }
        loadSearchHistory()
    }
    
    func clearSearchHistory() {
        SearchHistoryManager.shared.clearHistory()
        recentSearches = []
    }
}