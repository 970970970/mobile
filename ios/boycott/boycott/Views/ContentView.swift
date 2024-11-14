import SwiftUI

struct ContentView: View {
    @State private var isLoading = true
    
    var body: some View {
        ZStack {
            if isLoading {
                LaunchScreenView()
            } else {
                MainTabView()
            }
        }
        .onAppear {
            // 模拟启动加载时间
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                withAnimation {
                    isLoading = false
                }
            }
        }
    }
} 