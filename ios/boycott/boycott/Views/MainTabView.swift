import SwiftUI

struct MainTabView: View {
    @State private var selectedTab = 0
    @State private var selectedBrand: Brand?
    @State private var refreshID = UUID()
    @ObservedObject private var languageManager = LanguageManager.shared
    
    var body: some View {
        ZStack(alignment: .bottom) {
            TabView(selection: $selectedTab) {
                HomeView()
                    .tabItem {
                        Image(systemName: "house.fill")
                        Text("nav_home".localized)
                    }
                    .tag(0)
                
                BrandListView()
                    .tabItem {
                        Image(systemName: "tag.fill")
                        Text("nav_brands".localized)
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
                        Text("nav_articles".localized)
                    }
                    .tag(3)
                
                SettingsView()
                    .tabItem {
                        Image(systemName: "gearshape.fill")
                        Text("nav_settings".localized)
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
                        Text("nav_scan".localized)
                            .font(.caption2)
                    }
                    .foregroundColor(.white)
                }
            }
            .offset(y: -10)
            .padding(.bottom, 2)
        }
        .id(refreshID)
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
        .onReceive(NotificationCenter.default.publisher(for: LanguageManager.languageChangedNotification)) { _ in
            refreshID = UUID()
        }
    }
}
