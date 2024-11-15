import Foundation
import CoreData

class SearchHistoryManager {
    static let shared = SearchHistoryManager()
    private let maxHistoryItems = 20
    
    private lazy var persistentContainer: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "SearchHistory")
        container.loadPersistentStores { _, error in
            if let error = error {
                fatalError("Failed to load Core Data stack: \(error)")
            }
        }
        return container
    }()
    
    private var context: NSManagedObjectContext {
        persistentContainer.viewContext
    }
    
    private init() {}
    
    func addSearch(_ query: String) {
        // 删除已存在的相同搜索词
        let fetchRequest: NSFetchRequest<SearchHistoryItem> = SearchHistoryItem.fetchRequest()
        fetchRequest.predicate = NSPredicate(format: "query == %@", query)
        
        if let existingItems = try? context.fetch(fetchRequest) {
            existingItems.forEach { context.delete($0) }
        }
        
        // 添加新的搜索记录
        let newItem = SearchHistoryItem(context: context)
        newItem.query = query
        newItem.createdAt = Date()
        
        // 保持最大数量限制
        let allItemsFetch: NSFetchRequest<SearchHistoryItem> = SearchHistoryItem.fetchRequest()
        allItemsFetch.sortDescriptors = [NSSortDescriptor(key: "createdAt", ascending: false)]
        
        if let allItems = try? context.fetch(allItemsFetch), allItems.count >= maxHistoryItems {
            let itemsToDelete = allItems[maxHistoryItems - 1..<allItems.count]
            itemsToDelete.forEach { context.delete($0) }
        }
        
        // 保存更改
        try? context.save()
    }
    
    func getSearchHistory() -> [String] {
        let fetchRequest: NSFetchRequest<SearchHistoryItem> = SearchHistoryItem.fetchRequest()
        fetchRequest.sortDescriptors = [NSSortDescriptor(key: "createdAt", ascending: false)]
        
        guard let items = try? context.fetch(fetchRequest) else { return [] }
        return items.compactMap { $0.query }
    }
    
    func clearHistory() {
        let fetchRequest: NSFetchRequest<NSFetchRequestResult> = SearchHistoryItem.fetchRequest()
        let batchDeleteRequest = NSBatchDeleteRequest(fetchRequest: fetchRequest)
        
        do {
            try context.execute(batchDeleteRequest)
            try context.save()
        } catch {
            print("Error clearing history:", error)
        }
    }
} 