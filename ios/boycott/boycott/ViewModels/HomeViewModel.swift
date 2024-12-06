import Foundation

@MainActor
class HomeViewModel: ObservableObject {
    @Published var articles: [ArticleListItem] = []
    
    func loadData() {
        loadArticles()
    }
    
    private func loadArticles() {
        APIService.shared.fetchArticles(module: "index", page: 1) { [weak self] result in
            Task { @MainActor in
                switch result {
                case .success(let articles):
                    self?.articles = Array(articles.prefix(5))
                case .failure(let error):
                    print("❌ Error loading articles:", error)
                }
            }
        }
    }
}