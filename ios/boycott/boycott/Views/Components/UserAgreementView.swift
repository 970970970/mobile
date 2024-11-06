import SwiftUI

struct UserAgreementView: View {
    @State private var content: String = ""
    @State private var isLoading = true
    @State private var error: Error?
    
    var body: some View {
        Group {
            if isLoading {
                ProgressView()
            } else if let error = error {
                VStack {
                    Text("Error loading user agreement")
                    Text(error.localizedDescription)
                        .font(.caption)
                        .foregroundColor(.red)
                }
            } else {
                ScrollView {
                    MarkdownView(markdown: content)
                        .padding()
                }
            }
        }
        .navigationTitle("terms_of_service".localized)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            loadUserAgreement()
        }
    }
    
    private func loadUserAgreement() {
        APIService.shared.fetchAgreement(type: "user_agreement") { result in
            isLoading = false
            switch result {
            case .success(let article):
                print("Received content: \(article.content)")
                self.content = article.content
            case .failure(let error):
                print("Error loading agreement: \(error)")
                self.error = error
            }
        }
    }
}

#Preview {
    NavigationView {
        UserAgreementView()
    }
} 