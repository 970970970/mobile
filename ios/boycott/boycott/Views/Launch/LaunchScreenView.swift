import SwiftUI

struct LaunchScreenView: View {
    var body: some View {
        Image("launch-background")
            .resizable()
            .aspectRatio(contentMode: .fill)
            .edgesIgnoringSafeArea(.all)
    }
}

#Preview {
    LaunchScreenView()
} 