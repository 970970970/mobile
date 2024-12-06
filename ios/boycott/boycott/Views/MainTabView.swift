import SwiftUI

struct MainTabView: View {
    @State private var selectedTab = 0
    @State private var selectedBrand: Brand?
    @State private var refreshID = UUID()
    @State private var showingScanner = false
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
                
                // 扫描标签页只是一个占位符
                Color.clear
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
            Button(action: { showingScanner = true }) {
                ZStack {
                    Circle()
                        .fill(Color.blue)
                        .frame(width: 56, height: 56)
                        .shadow(radius: 4)
                    
                    Image(systemName: "barcode.viewfinder")
                        .font(.system(size: 24))
                        .foregroundColor(.white)
                }
            }
            .offset(y: -8) // 稍微向上偏移
        }
        .sheet(isPresented: $showingScanner) {
            ScanView(isPresented: $showingScanner)
        }
        .onReceive(NotificationCenter.default.publisher(for: .switchToTab)) { notification in
            if let tabIndex = notification.object as? Int {
                selectedTab = tabIndex
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
        .id(refreshID)
        .sheet(item: $selectedBrand) { brand in
            BrandDetailView(brand: brand, isPresented: $selectedBrand)
        }
    }
}
