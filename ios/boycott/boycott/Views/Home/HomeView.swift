import SwiftUI

struct HomeView: View {
    @State private var searchText = ""
    @State private var showingSearchHistory = false
    @State private var showingScanner = false
    @StateObject private var viewModel = HomeViewModel()
    @StateObject private var brandListViewModel = HomeBrandListViewModel()
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 搜索栏固定在顶部
                SearchBarView(
                    searchText: $searchText,
                    showingSearchHistory: $showingSearchHistory,
                    showingScanner: $showingScanner
                )
                .padding(.horizontal)
                .padding(.vertical, 8)
                .background(Color(.systemBackground))
                
                // 可滚动内容
                ScrollView {
                    VStack(spacing: 16) {
                        // 文章轮播
                        if !viewModel.articles.isEmpty {
                            ArticleCarouselView(articles: viewModel.articles)
                                .frame(height: 200)
                        }
                        
                        // 品牌列表标题
                        if !brandListViewModel.brands.isEmpty {
                            HStack {
                                Text("home_popular_brands".localized)
                                    .font(.headline)
                                Spacer()
                                Button(action: {
                                    NotificationCenter.default.post(name: .switchToTab, object: 1)
                                }) {
                                    Text("home_view_more".localized)
                                        .foregroundColor(.blue)
                                }
                            }
                            .padding(.horizontal)
                            
                            // 品牌网格
                            BrandGridView(viewModel: brandListViewModel)
                        }
                        
                        // 加载状态和结束提示
                        if brandListViewModel.isLoading {
                            HStack {
                                Spacer()
                                ProgressView()
                                Spacer()
                            }
                            .padding()
                        } else if !brandListViewModel.hasMorePages {
                            Text("home_end_of_list".localized)
                                .foregroundColor(.gray)
                                .frame(maxWidth: .infinity)
                                .padding()
                        }
                    }
                }
                .refreshable {
                    await viewModel.loadData()
                    await brandListViewModel.refresh()
                }
            }
            .navigationBarHidden(true)
            .sheet(isPresented: $showingSearchHistory) {
                SearchHistoryView(searchText: $searchText, isPresented: $showingSearchHistory)
            }
            .sheet(isPresented: $showingScanner) {
                ScanView(isPresented: $showingScanner)
            }
            .onAppear {
                if viewModel.articles.isEmpty {
                    viewModel.loadData()
                }
                if brandListViewModel.brands.isEmpty {
                    brandListViewModel.loadBrands()
                }
            }
        }
    }
}

struct ScrollData: Equatable {
    let contentHeight: CGFloat
    let scrollOffset: CGFloat
    
    static func == (lhs: ScrollData, rhs: ScrollData) -> Bool {
        return lhs.contentHeight == rhs.contentHeight && lhs.scrollOffset == rhs.scrollOffset
    }
}

struct ScrollOffsetPreferenceKey: PreferenceKey {
    static var defaultValue = ScrollData(contentHeight: 0, scrollOffset: 0)
    static func reduce(value: inout ScrollData, nextValue: () -> ScrollData) {
        value = nextValue()
    }
}

struct SearchBarView: View {
    @Binding var searchText: String
    @Binding var showingSearchHistory: Bool
    @Binding var showingScanner: Bool
    @StateObject private var viewModel = SearchViewModel()
    
    var body: some View {
        HStack(spacing: 0) {
            // 搜索框主体
            ZStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color(.systemGray6))
                    .frame(height: 44)
                    .onTapGesture {
                        showingSearchHistory = true
                    }
                
                HStack(spacing: 12) {
                    // 扫码图标
                    Button(action: { showingScanner = true }) {
                        Image(systemName: "barcode.viewfinder")
                            .foregroundColor(.blue)
                    }
                    .padding(.leading, 12)
                    
                    // 搜索框
                    Text(viewModel.currentSuggestion)
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .onTapGesture {
                            showingSearchHistory = true
                        }
                    
                    // 相机按钮
                    Button(action: { showingScanner = true }) {
                        Image(systemName: "camera")
                            .foregroundColor(.blue)
                    }
                    
                    // 分隔线
                    Rectangle()
                        .fill(Color(.systemGray4))
                        .frame(width: 1, height: 24)
                    
                    // 搜索按钮
                    Button(action: {
                        performSearch(viewModel.currentSuggestion)
                    }) {
                        Text("button_search".localized)
                            .foregroundColor(.blue)
                    }
                    .padding(.horizontal, 12)
                }
            }
        }
        .onAppear {
            viewModel.loadHotSearches()
        }
    }
    
    private func performSearch(_ query: String) {
        guard !query.isEmpty else { return }
        
        // 保存搜索历史
        SearchHistoryManager.shared.addSearch(query)
        
        // 调用搜索接口
        APIService.shared.searchBrands(keyword: query) { result in
            switch result {
            case .success(let response):
                if response.total == 1, let brand = response.items.first {
                    // 如果只有一个结果，显示品牌详情
                    NotificationCenter.default.post(
                        name: .showBrandDetail,
                        object: brand
                    )
                } else if response.total > 1 {
                    // 如果有多个结果，切换到列表页并显示搜索结果
                    NotificationCenter.default.post(name: .switchToTab, object: 1)
                    // 稍微延迟一下发送搜索通知，确保标签切换完成
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                        NotificationCenter.default.post(
                            name: .performSearch,
                            object: query
                        )
                    }
                } else {
                    // 没有搜索结果，显示提示
                    // TODO: 显示无结果提示
                }
            case .failure(let error):
                print("Search error:", error)
                // TODO: 显示错误提示
            }
        }
    }
}