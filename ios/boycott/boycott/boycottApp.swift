//
//  boycottApp.swift
//  boycott
//
//  Created by 马思奇 on 2024/10/30.
//

import SwiftUI
import CoreData

@main
struct boycottApp: App {
    // 使用 AppStorage 来管理主题设置
    @AppStorage("theme") private var isDarkMode = false
    
    let persistentContainer = NSPersistentContainer(name: "SearchHistory")
    
    init() {
        persistentContainer.loadPersistentStores { description, error in
            if let error = error {
                print("Core Data failed to load: \(error.localizedDescription)")
            }
        }
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .preferredColorScheme(isDarkMode ? .dark : .light)
                .environment(\.managedObjectContext, persistentContainer.viewContext)
        }
    }
}

