import SwiftUI
import Foundation

struct ContentView: View {
    static let languageChangedNotification = Notification.Name("com.boycott.languageChanged")
    
    @StateObject private var languageManager = LanguageManager.shared
    @State private var selectedTab = 0
    @State private var refreshFlag = false
    
    var body: some View {
        TabView(selection: $selectedTab) {
            HomeView()
                .tabItem {
                    Label("home".localized, systemImage: "house.fill")
                }
                .tag(0)
            
            BrandListView()
                .tabItem {
                    Label("brands".localized, systemImage: "tag.fill")
                }
                .tag(1)
            
            ScanView()
                .tabItem {
                    Label("scan".localized, systemImage: "camera.fill")
                }
                .tag(2)
            
            SettingsView()
                .tabItem {
                    Label("settings".localized, systemImage: "gearshape.fill")
                }
                .tag(3)
        }
        .id(refreshFlag)
        .environment(\.locale, languageManager.locale)
        .supportRTL()
        .onReceive(NotificationCenter.default.publisher(for: ContentView.languageChangedNotification)) { _ in
            print("📢 [ContentView] Received language change notification")
            print("🔄 [ContentView] Current locale: \(languageManager.locale.identifier)")
            print("🔄 [ContentView] Toggling refresh flag from \(refreshFlag) to \(!refreshFlag)")
            refreshFlag.toggle()
        }
        .onAppear {
            print("👋 [ContentView] View appeared with locale: \(languageManager.locale.identifier)")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}