import SwiftUI

struct ErrorView: View {
    let error: Error
    let retryAction: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "exclamationmark.triangle")
                .font(.system(size: 50))
                .foregroundColor(.red)
            
            Text("出错了")
                .font(.title)
            
            Text(error.localizedDescription)
                .multilineTextAlignment(.center)
                .foregroundColor(.gray)
            
            Button(action: retryAction) {
                Text("重试")
                    .bold()
                    .frame(width: 100)
                    .padding()
                    .background(Color.accentColor)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
        }
        .padding()
    }
} 