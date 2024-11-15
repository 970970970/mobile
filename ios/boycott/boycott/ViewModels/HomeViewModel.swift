import Foundation

@MainActor
class HomeViewModel: ObservableObject {
    @Published var articles: [ArticleListItem] = []
    @Published var brands: [Brand] = []
    @Published var isLoadingMore = false
    @Published var hasReachedEnd = false
    
    private var currentPage = 1
    private let pageSize = 20
    
    func loadInitialData() {
        loadArticles()
        loadBrands()
    }
    
    func loadMoreBrands() async {
        guard !isLoadingMore && !hasReachedEnd else { return }
        
        isLoadingMore = true
        print("📥 Loading more brands, page: \(currentPage)")
        
        BrandService.shared.fetchBrands(page: currentPage, pageSize: pageSize) { [weak self] result in
            Task { @MainActor in
                guard let self = self else { return }
                self.isLoadingMore = false
                
                switch result {
                case .success(let response):
                    print("✅ Loaded \(response.items.count) more brands")
                    if response.items.isEmpty {
                        print("🏁 Reached the end")
                        self.hasReachedEnd = true
                    } else {
                        self.currentPage += 1
                        self.brands.append(contentsOf: response.items)
                    }
                case .failure(let error):
                    print("❌ Error loading more brands:", error)
                }
            }
        }
    }
    
    private func loadArticles() {
        APIService.shared.fetchArticles(module: "index", page: 1) { [weak self] result in
            Task { @MainActor in
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
        BrandService.shared.fetchBrands(page: 1, pageSize: pageSize) { [weak self] result in
            Task { @MainActor in
                switch result {
                case .success(let response):
                    self?.brands = response.items
                case .failure(let error):
                    print("Error loading brands:", error)
                }
            }
        }
    }
} 