import Foundation

@MainActor
class BrandListViewModel: ObservableObject {
    @Published var brands: [Brand] = []
    @Published var isLoading = false
    @Published var error: Error?
    @Published var hasMorePages = true
    
    private var currentPage = 1
    private var lastSearchKeyword = ""
    private let pageSize = 20
    private var isFetching = false
    
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
    
    func loadBrands(loadMore: Bool = false) {
        guard !isFetching else { return }
        guard loadMore ? hasMorePages : true else { return }
        
        if !loadMore {
            currentPage = 1
            hasMorePages = true
        }
        
        isFetching = true
        isLoading = !loadMore // 只在首次加载时显示loading状态
        
        print("🔄 [BrandListViewModel] Loading brands, page: \(currentPage), keyword: \(lastSearchKeyword)")
        
        BrandService.shared.fetchBrands(
            keyword: lastSearchKeyword,
            page: currentPage,
            pageSize: pageSize
        ) { [weak self] result in
            guard let self = self else { return }
            
            self.isFetching = false
            self.isLoading = false
            
            switch result {
            case .success(let response):
                print("✅ [BrandListViewModel] Loaded \(response.items.count) brands")
                if self.currentPage == 1 {
                    self.brands = response.items
                } else {
                    self.brands.append(contentsOf: response.items)
                }
                
                // 检查是否还有更多页面
                self.hasMorePages = response.items.count >= self.pageSize
                if self.hasMorePages {
                    self.currentPage += 1
                }
                
            case .failure(let error):
                print("❌ [BrandListViewModel] Error loading brands:", error.localizedDescription)
                self.error = error
            }
        }
    }
    
    func loadMoreIfNeeded(currentItem item: Brand) {
        let thresholdIndex = brands.index(brands.endIndex, offsetBy: -5)
        if let itemIndex = brands.firstIndex(where: { $0.id == item.id }),
           itemIndex >= thresholdIndex {
            loadBrands(loadMore: true)
        }
    }
    
    func search(keyword: String) {
        lastSearchKeyword = keyword
        loadBrands()
    }
    
    func refresh() {
        loadBrands()
    }
}
