import SwiftUI

struct MainTabView: View {
    var body: some View {
        TabView {
            HomeView()
                .tabItem {
                    Label("首页", systemImage: "house")
                }
            
            BrandListView()
                .tabItem {
                    Label("品牌", systemImage: "tag")
                }
            
            ScanView()
                .tabItem {
                    Label("扫描", systemImage: "camera")
                }
            
            SettingsView()
                .tabItem {
                    Label("设置", systemImage: "gear")
                }
        }
    }
} 