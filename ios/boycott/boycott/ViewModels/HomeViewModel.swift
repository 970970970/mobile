import Foundation

class HomeViewModel: ObservableObject {
    @Published var articles: [ArticleListItem] = []
    @Published var brands: [Brand] = []
    @Published var isLoadingMore = false
    @Published var hasReachedEnd = false
    
    private var currentPage = 1
    private let pageSize = 10
    
    func loadInitialData() {
        loadArticles()
        loadBrands()
    }
    
    func refresh() async {
        await MainActor.run {
            currentPage = 1
            hasReachedEnd = false
            brands = []
        }
        loadInitialData()
    }
    
    func loadMoreBrands() async {
        guard !isLoadingMore && !hasReachedEnd else {
            print("⚠️ Skip loading more: isLoadingMore=\(isLoadingMore), hasReachedEnd=\(hasReachedEnd)")
            return
        }
        
        await MainActor.run {
            isLoadingMore = true
        }
        
        print("📱 Loading more brands, page: \(currentPage + 1)")
        
        BrandService.shared.fetchBrands(page: currentPage + 1, pageSize: pageSize) { [weak self] result in
            guard let self = self else { return }
            
            Task { @MainActor in
                switch result {
                case .success(let response):
                    print("✅ Loaded \(response.data.items.count) more brands")
                    if response.data.items.isEmpty {
                        print("🏁 Reached the end")
                        self.hasReachedEnd = true
                    } else {
                        self.currentPage += 1
                        self.brands.append(contentsOf: response.data.items)
                    }
                case .failure(let error):
                    print("❌ Error loading more brands:", error)
                }
                self.isLoadingMore = false
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
                    self?.brands = response.data.items
                case .failure(let error):
                    print("Error loading brands:", error)
                }
            }
        }
    }
} 