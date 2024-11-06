import SwiftUI

struct LoadingView: View {
    let text: String
    
    var body: some View {
        VStack(spacing: 16) {
            ProgressView()
                .scaleEffect(1.5)
            
            Text(text)
                .foregroundColor(.gray)
        }
    }
} 