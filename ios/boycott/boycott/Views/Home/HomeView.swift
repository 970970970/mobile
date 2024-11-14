import SwiftUI

struct HomeView: View {
    @State private var searchText = ""
    @State private var showingSearchHistory = false
    @State private var showingScanner = false
    @StateObject private var viewModel = HomeViewModel()
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 16) {
                    // 搜索栏
                    SearchBarView(
                        searchText: $searchText,
                        showingSearchHistory: $showingSearchHistory,
                        showingScanner: $showingScanner
                    )
                    .padding(.horizontal)
                    
                    // 文章轮播
                    if !viewModel.articles.isEmpty {
                        ArticleCarouselView(articles: viewModel.articles)
                            .frame(height: 200)
                    }
                    
                    // 品牌列表标题
                    if !viewModel.brands.isEmpty {
                        HStack {
                            Text("热门品牌")
                                .font(.headline)
                            Spacer()
                            Button(action: {
                                NotificationCenter.default.post(name: .switchToTab, object: 1)
                            }) {
                                Text("查看更多")
                                    .foregroundColor(.blue)
                            }
                        }
                        .padding(.horizontal)
                        
                        // 品牌网格
                        BrandGridView(brands: viewModel.brands)
                            .padding(.horizontal)
                    }
                    
                    // 加载更多指示器
                    if viewModel.isLoadingMore {
                        HStack {
                            Spacer()
                            ProgressView()
                                .padding()
                            Spacer()
                        }
                    } else if viewModel.hasReachedEnd {
                        Text("已经到底啦")
                            .foregroundColor(.gray)
                            .padding()
                    }
                }
            }
            .coordinateSpace(name: "scroll")
            .background(
                GeometryReader { proxy in
                    Color.clear.preference(
                        key: ScrollOffsetPreferenceKey.self,
                        value: ScrollData(
                            contentHeight: proxy.frame(in: .named("scroll")).height,
                            scrollOffset: proxy.frame(in: .named("scroll")).minY
                        )
                    )
                }
            )
            .onPreferenceChange(ScrollOffsetPreferenceKey.self) { data in
                let threshold: CGFloat = 100
                let position = -data.scrollOffset + UIScreen.main.bounds.height
                let target = data.contentHeight - threshold
                
                if position > target && !viewModel.isLoadingMore && !viewModel.hasReachedEnd {
                    print("🔄 Triggering load more...")
                    Task {
                        await viewModel.loadMoreBrands()
                    }
                }
            }
            .sheet(isPresented: $showingSearchHistory) {
                SearchHistoryView()
            }
            .sheet(isPresented: $showingScanner) {
                ScanView()
            }
            .onAppear {
                viewModel.loadInitialData()
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
    @State private var brandSuggestions: [String] = ["可口可乐", "麦当劳", "星巴克", "耐克", "阿迪达斯"]
    @State private var currentSuggestionIndex = 0
    let timer = Timer.publish(every: 5, on: .main, in: .common).autoconnect()
    
    var body: some View {
        HStack(spacing: 0) {
            // 搜索框主体
            ZStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color(.systemGray6))
                    .frame(height: 44)
                
                HStack(spacing: 12) {
                    // 扫码图标
                    Button(action: { showingScanner = true }) {
                        Image(systemName: "barcode.viewfinder")
                            .foregroundColor(.blue)
                    }
                    .padding(.leading, 12)
                    
                    // 搜索框
                    Text(brandSuggestions[currentSuggestionIndex])
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
                    Button(action: performSearch) {
                        Text("搜索")
                            .foregroundColor(.blue)
                    }
                    .padding(.horizontal, 12)
                }
            }
        }
        .onReceive(timer) { _ in
            withAnimation {
                currentSuggestionIndex = (currentSuggestionIndex + 1) % brandSuggestions.count
            }
        }
    }
    
    private func performSearch() {
        // TODO: 实现搜索功能
    }
} 