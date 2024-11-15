import Foundation

@MainActor
class BrandListViewModel: ObservableObject {
    @Published var brands: [Brand] = []
    @Published var isLoading = false
    @Published var error: Error?
    
    private var currentPage = 1
    private var hasMorePages = true
    private var lastSearchKeyword = ""
    private let pageSize = 20
    
    init() {
        setupNotifications()
    }
    
    private func setupNotifications() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleSearch),
            name: .performSearch,
            object: nil
        )
    }
    
    @objc private func handleSearch(_ notification: Notification) {
        if let keyword = notification.object as? String {
            search(keyword: keyword)
        }
    }
    
    func loadBrands() {
        guard !isLoading else { return }
        isLoading = true
        
        print("🔄 [BrandListViewModel] Loading brands, page: \(currentPage), keyword: \(lastSearchKeyword)")
        
        BrandService.shared.fetchBrands(
            keyword: lastSearchKeyword,
            page: currentPage,
            pageSize: pageSize
        ) { [weak self] result in
            guard let self = self else { return }
            
            self.isLoading = false
            
            switch result {
            case .success(let response):
                print("✅ [BrandListViewModel] Loaded \(response.items.count) brands")
                if self.currentPage == 1 {
                    self.brands = response.items
                } else {
                    self.brands.append(contentsOf: response.items)
                }
                
                self.hasMorePages = response.items.count == self.pageSize
                self.currentPage += 1
                
            case .failure(let error):
                print("❌ [BrandListViewModel] Error loading brands: \(error.localizedDescription)")
                self.error = error
            }
        }
    }
    
    func loadMoreIfNeeded() {
        guard hasMorePages else { return }
        print("📥 [BrandListViewModel] Loading more brands")
        loadBrands()
    }
    
    func refresh() {
        print("🔄 [BrandListViewModel] Refreshing brands list")
        currentPage = 1
        hasMorePages = true
        loadBrands()
    }
    
    func search(keyword: String) {
        print("🔍 [BrandListViewModel] Searching with keyword: \(keyword)")
        currentPage = 1
        hasMorePages = true
        lastSearchKeyword = keyword
        loadBrands()
    }
} 
