import SwiftUI

struct ArticleListView: View {
    @StateObject private var viewModel = ArticleListViewModel()
    
    var body: some View {
        NavigationView {
            List {
                ForEach(viewModel.articles) { article in
                    NavigationLink {
                        if let detailArticle = viewModel.articleDetails[article.id] {
                            ArticleDetailView(article: detailArticle)
                        } else {
                            ProgressView()
                                .onAppear {
                                    viewModel.loadArticleDetail(id: article.id)
                                }
                        }
                    } label: {
                        ArticleRowView(article: article)
                    }
                }
                
                if viewModel.isLoading {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                        .listRowSeparator(.hidden)
                }
            }
            .listStyle(.plain)
            .refreshable {
                await viewModel.refresh()
            }
            .navigationTitle("nav_articles".localized)
        }
        .onAppear {
            viewModel.loadArticles()
        }
    }
}

struct ArticleRowView: View {
    let article: ArticleListItem
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let imageUrl = article.image {
                AsyncImage(url: URL(string: imageUrl)) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                } placeholder: {
                    Color.gray
                }
                .frame(height: 150)
                .cornerRadius(8)
            }
            
            Text(article.title)
                .font(.headline)
                .lineLimit(2)
            
            Text(article.summary)
                .font(.subheadline)
                .foregroundColor(.gray)
                .lineLimit(2)
        }
        .padding(.vertical, 8)
    }
}

class ArticleListViewModel: ObservableObject {
    @Published var articles: [ArticleListItem] = []
    @Published var isLoading = false
    @Published var articleDetails: [Int: Article] = [:]
    private var currentPage = 1
    
    func loadArticles() {
        guard !isLoading else { return }
        isLoading = true
        
        APIService.shared.fetchArticles(module: "index", page: currentPage) { [weak self] result in
            guard let self = self else { return }
            
            DispatchQueue.main.async {
                self.isLoading = false
                
                switch result {
                case .success(let articles):
                    if self.currentPage == 1 {
                        self.articles = articles
                    } else {
                        self.articles.append(contentsOf: articles)
                    }
                    self.currentPage += 1
                    
                case .failure(let error):
                    print("Error loading articles:", error)
                }
            }
        }
    }
    
    func loadArticleDetail(id: Int) {
        guard articleDetails[id] == nil else { return }
        
        APIService.shared.fetchArticle(id: id) { [weak self] result in
            guard let self = self else { return }
            
            DispatchQueue.main.async {
                switch result {
                case .success(let article):
                    self.articleDetails[id] = article
                case .failure(let error):
                    print("Error loading article detail:", error)
                }
            }
        }
    }
    
    func refresh() async {
        currentPage = 1
        loadArticles()
    }
} 