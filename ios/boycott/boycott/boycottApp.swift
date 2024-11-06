//
//  boycottApp.swift
//  boycott
//
//  Created by 马思奇 on 2024/10/30.
//

import SwiftUI

@main
struct boycottApp: App {
    // 使用 AppStorage 来管理主题设置
    @AppStorage("theme") private var isDarkMode = false
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .preferredColorScheme(isDarkMode ? .dark : .light)
        }
    }
}
