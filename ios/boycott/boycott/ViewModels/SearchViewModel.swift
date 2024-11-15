import Foundation
import Combine
import SwiftUI

class SearchViewModel: ObservableObject {
    @Published var hotSearches: [String] = []
    @Published var currentSuggestionIndex = 0
    private var timer: Timer?
    
    var currentSuggestion: String {
        guard !hotSearches.isEmpty else { return "" }
        return hotSearches[currentSuggestionIndex]
    }
    
    init() {
        setupTimer()
    }
    
    func loadHotSearches() {
        // 调用API获取热门搜索
        APIService.shared.fetchHotSearches { [weak self] result in
            DispatchQueue.main.async {
                switch result {
                case .success(let searches):
                    self?.hotSearches = searches
                case .failure(let error):
                    print("Error loading hot searches:", error)
                    // 设置一些默认值以防API失败
                    self?.hotSearches = ["可口可乐", "麦当劳", "星巴克"]
                }
            }
        }
    }
    
    private func setupTimer() {
        timer = Timer.scheduledTimer(withTimeInterval: 5.0, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            withAnimation {
                self.currentSuggestionIndex = (self.currentSuggestionIndex + 1) % max(1, self.hotSearches.count)
            }
        }
    }
    
    deinit {
        timer?.invalidate()
    }
} 