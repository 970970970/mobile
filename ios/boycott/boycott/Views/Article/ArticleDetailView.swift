import SwiftUI
import Down

struct ArticleDetailView: View {
    let article: Article
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text(article.title)
                    .font(.title)
                    .bold()
                
                if let markdown = try? Down(markdownString: article.content).toAttributedString() {
                    Text(AttributedString(markdown))
                        .font(.body)
                } else {
                    Text(article.content)
                        .font(.body)
                }
            }
            .padding()
        }
        .navigationBarTitleDisplayMode(.inline)
    }
} 