import SwiftUI

struct PrivacyPolicyView: View {
    @State private var content: String = ""
    @State private var isLoading = true
    @State private var error: Error?
    
    var body: some View {
        Group {
            if isLoading {
                ProgressView()
            } else if let error = error {
                VStack {
                    Text("Error loading privacy policy")
                    Text(error.localizedDescription)
                        .font(.caption)
                        .foregroundColor(.red)
                }
            } else {
                MarkdownView(markdown: content)
            }
        }
        .navigationTitle("privacy_policy".localized)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            loadPrivacyPolicy()
        }
    }
    
    private func loadPrivacyPolicy() {
        APIService.shared.fetchPrivacyPolicy { result in
            isLoading = false
            switch result {
            case .success(let article):
                print("Received content: \(article.content)")
                self.content = article.content
            case .failure(let error):
                print("Error loading policy: \(error)")
                self.error = error
            }
        }
    }
}

#Preview {
    NavigationView {
        PrivacyPolicyView()
    }
} 