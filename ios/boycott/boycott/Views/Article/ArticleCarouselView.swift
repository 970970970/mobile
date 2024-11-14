import SwiftUI

struct ArticleCarouselView: View {
    let articles: [ArticleListItem]
    @State private var currentIndex = 0
    @StateObject private var viewModel = ArticleCarouselViewModel()
    let timer = Timer.publish(every: 5, on: .main, in: .common).autoconnect()
    
    var body: some View {
        TabView(selection: $currentIndex) {
            ForEach(Array(articles.enumerated()), id: \.element.id) { index, article in
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
                    ArticleCard(article: article)
                        .tag(index)
                }
            }
        }
        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .automatic))
        .onReceive(timer) { _ in
            withAnimation {
                currentIndex = (currentIndex + 1) % articles.count
            }
        }
    }
}

class ArticleCarouselViewModel: ObservableObject {
    @Published var articleDetails: [Int: Article] = [:]
    
    func loadArticleDetail(id: Int) {
        guard articleDetails[id] == nil else { return }
        
        APIService.shared.fetchArticle(id: id) { [weak self] result in
            DispatchQueue.main.async {
                switch result {
                case .success(let article):
                    self?.articleDetails[id] = article
                case .failure(let error):
                    print("Error loading article detail:", error)
                }
            }
        }
    }
}

struct ArticleCard: View {
    let article: ArticleListItem
    @State private var detailArticle: Article?
    @State private var isLoading = false
    
    var body: some View {
        NavigationLink {
            if let detailArticle = detailArticle {
                ArticleDetailView(article: detailArticle)
            } else {
                VStack {
                    ProgressView()
                    Text("加载中...")
                        .foregroundColor(.gray)
                }
                .onAppear {
                    loadArticleDetail()
                }
            }
        } label: {
            ZStack(alignment: .bottom) {
                if let imageUrl = article.image {
                    AsyncImage(url: URL(string: imageUrl)) { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    } placeholder: {
                        Color.gray
                    }
                    .frame(height: 200)
                    .clipped()
                }
                
                LinearGradient(
                    gradient: Gradient(colors: [.clear, .black.opacity(0.7)]),
                    startPoint: .top,
                    endPoint: .bottom
                )
                
                Text(article.title)
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding()
                    .multilineTextAlignment(.leading)
            }
        }
    }
    
    private func loadArticleDetail() {
        guard !isLoading else { return }
        isLoading = true
        
        APIService.shared.fetchArticle(id: article.id) { result in
            isLoading = false
            switch result {
            case .success(let article):
                detailArticle = article
            case .failure(let error):
                print("Error loading article detail:", error)
            }
        }
    }
} 