import SwiftUI

struct MainTabView: View {
    @State private var selectedTab = 0
    @State private var selectedBrand: Brand?
    
    var body: some View {
        ZStack(alignment: .bottom) {
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
                        Text("")
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
            
            // 凸起的扫描按钮
            Button(action: { selectedTab = 2 }) {
                ZStack {
                    Circle()
                        .fill(Color.blue)
                        .frame(width: 56, height: 56)
                        .shadow(radius: 3, x: 0, y: 2)
                    
                    VStack(spacing: 2) {
                        Image(systemName: "camera.viewfinder")
                            .font(.system(size: 24))
                        Text("扫描")
                            .font(.caption2)
                    }
                    .foregroundColor(.white)
                }
            }
            .offset(y: -10)
            .padding(.bottom, 2)
        }
        .sheet(item: $selectedBrand) { brand in
            BrandDetailView(brand: brand, isPresented: $selectedBrand)
        }
        .onReceive(NotificationCenter.default.publisher(for: .switchToTab)) { notification in
            if let tab = notification.object as? Int {
                selectedTab = tab
            }
        }
        .onReceive(NotificationCenter.default.publisher(for: .showBrandDetail)) { notification in
            if let brand = notification.object as? Brand {
                selectedBrand = brand
            }
        }
    }
}
