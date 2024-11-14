import SwiftUI

struct MainTabView: View {
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            HomeView()
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("首页")
                }
                .tag(0)
            
            BrandListView()
                .tabItem {
                    Image(systemName: "tag.fill")
                    Text("品牌")
                }
                .tag(1)
            
            ScanView()
                .tabItem {
                    Image(systemName: "camera.viewfinder")
                    Text("扫描")
                }
                .tag(2)
            
            ArticleListView()
                .tabItem {
                    Image(systemName: "newspaper.fill")
                    Text("文章")
                }
                .tag(3)
            
            SettingsView()
                .tabItem {
                    Image(systemName: "gearshape.fill")
                    Text("设置")
                }
                .tag(4)
        }
    }
}