import Foundation

class HomeViewModel: ObservableObject {
    @Published var articles: [ArticleListItem] = []
    @Published var brands: [Brand] = []
    
    func loadInitialData() {
        loadArticles()
        loadBrands()
    }
    
    private func loadArticles() {
        APIService.shared.fetchArticles(module: "index", page: 1) { [weak self] result in
            DispatchQueue.main.async {
                switch result {
                case .success(let articles):
                    self?.articles = Array(articles.prefix(5))
                case .failure(let error):
                    print("Error loading articles:", error)
                }
            }
        }
    }
    
    private func loadBrands() {
        BrandService.shared.fetchBrands(page: 1, pageSize: 10) { [weak self] result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    self?.brands = response.data.items
                case .failure(let error):
                    print("Error loading brands:", error)
                }
            }
        }
    }
} 