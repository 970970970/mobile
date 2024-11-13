import SwiftUI

struct HomeView: View {
    @State private var searchText = ""
    @State private var articles: [ArticleListItem] = []
    @State private var isLoading = false
    @State private var currentPage = 1
    @State private var hasMoreData = true
    @State private var selectedArticleId: Int? = nil
    @StateObject private var languageManager = LanguageManager.shared
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 搜索框
                SearchBar(text: $searchText, onSubmit: performSearch)
                    .padding()
                    .background(Color(.systemBackground))
                    .shadow(color: .gray.opacity(0.2), radius: 5, x: 0, y: 2)
                
                // 文章列表
                if isLoading && articles.isEmpty {
                    ProgressView()
                        .scaleEffect(1.5)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if articles.isEmpty {
                    EmptyStateView()
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(articles) { article in
                                ArticleListItemView(article: article)
                                    .padding(.horizontal)
                                    .onTapGesture {
                                        selectedArticleId = article.id
                                    }
                            }
                            
                            if hasMoreData {
                                ProgressView()
                                    .padding()
                                    .onAppear {
                                        loadMoreArticles()
                                    }
                            }
                        }
                        .padding(.vertical)
                    }
                    .refreshable {
                        await refreshArticles()
                    }
                }
            }
            .navigationTitle(Text("home".localized))
            .navigationBarTitleDisplayMode(.large)
            .sheet(item: Binding(
                get: { selectedArticleId.map { ArticleIdentifier(id: $0) } },
                set: { selectedArticleId = $0?.id }
            )) { identifier in
                NavigationView {
                    ArticleDetailLoadingView(articleId: identifier.id)
                }
            }
        }
        .environment(\.locale, languageManager.locale)
        .supportRTL()
        .onAppear {
            if languageManager.languageChanged {
                print("🔄 [HomeView] Language changed detected, refreshing articles")
                Task {
                    await refreshArticles()
                }
                languageManager.resetLanguageChanged()
            } else if articles.isEmpty {
                print("📚 [HomeView] No articles, loading initial data")
                loadArticles()
            }
        }
    }
    
    private func loadArticles() {
        guard !isLoading else { return }
        isLoading = true
        
        print("📚 [HomeView] Loading articles for page \(currentPage)")
        APIService.shared.fetchArticles(page: currentPage) { result in
            isLoading = false
            switch result {
            case .success(let newArticles):
                print("✅ [HomeView] Loaded \(newArticles.count) articles")
                articles.append(contentsOf: newArticles)
                hasMoreData = !newArticles.isEmpty
                currentPage += 1
            case .failure(let error):
                print("❌ [HomeView] Error loading articles: \(error)")
            }
        }
    }
    
    private func loadMoreArticles() {
        loadArticles()
    }
    
    private func refreshArticles() async {
        currentPage = 1
        articles = []
        hasMoreData = true
        loadArticles()
    }
    
    private func performSearch() {
        print("🔍 [HomeView] Performing search with text: \(searchText)")
        currentPage = 1
        articles = []
        hasMoreData = true
        loadArticles()
    }
}

// 用于 sheet 绑定的辅助结构体
struct ArticleIdentifier: Identifiable {
    let id: Int
}

// 文章列表项视图
struct ArticleListItemView: View {
    let article: ArticleListItem
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            if let imageUrl = article.imageUrl {
                AsyncImage(url: imageUrl) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(height: 200)
                        .clipped()
                        .cornerRadius(12)
                } placeholder: {
                    Rectangle()
                        .fill(Color.gray.opacity(0.2))
                        .frame(height: 200)
                        .cornerRadius(12)
                }
            }
            
            VStack(alignment: .leading, spacing: 8) {
                Text(article.title)
                    .font(.headline)
                    .lineLimit(2)
                
                Text(article.summary)
                    .font(.subheadline)
                    .foregroundColor(.gray)
                    .lineLimit(3)
                
                HStack {
                    Text(article.formattedDate)
                        .font(.caption)
                        .foregroundColor(.gray)
                    
                    Spacer()
                    
                    Image(systemName: "arrow.right")
                        .foregroundColor(.blue)
                }
            }
            .padding(.horizontal, 4)
        }
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .gray.opacity(0.2), radius: 5, x: 0, y: 2)
    }
}

// 文章详情加载视图
struct ArticleDetailLoadingView: View {
    let articleId: Int
    @State private var article: Article?
    @State private var isLoading = true
    @State private var error: Error?
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        Group {
            if isLoading {
                ProgressView()
            } else if let error = error {
                VStack {
                    Text("Error loading article")
                    Text(error.localizedDescription)
                        .font(.caption)
                        .foregroundColor(.red)
                }
            } else if let article = article {
                ArticleDetailView(article: article)
            }
        }
        .onAppear {
            loadArticle()
        }
    }
    
    private func loadArticle() {
        APIService.shared.fetchArticle(id: articleId) { result in
            isLoading = false
            switch result {
            case .success(let article):
                self.article = article
            case .failure(let error):
                self.error = error
            }
        }
    }
}

struct SearchBar: View {
    @Binding var text: String
    var onSubmit: () -> Void
    
    var body: some View {
        HStack {
            TextField("search_placeholder".localized, text: $text)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .onSubmit(onSubmit)
            
            Button(action: onSubmit) {
                Image(systemName: "magnifyingglass")
            }
        }
    }
}

struct EmptyStateView: View {
    var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "doc.text.image")
                .font(.system(size: 60))
                .foregroundColor(.gray)
            
            Text("no_articles".localized)
                .font(.headline)
                .foregroundColor(.gray)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    HomeView()
}
