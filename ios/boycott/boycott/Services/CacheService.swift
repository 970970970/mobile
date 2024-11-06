import Foundation

class CacheService {
    static let shared = CacheService()
    private let cache = NSCache<NSString, AnyObject>()
    
    private init() {
        cache.countLimit = 100
    }
    
    func set(_ object: AnyObject, forKey key: String) {
        cache.setObject(object, forKey: key as NSString)
    }
    
    func get(forKey key: String) -> AnyObject? {
        return cache.object(forKey: key as NSString)
    }
    
    func remove(forKey key: String) {
        cache.removeObject(forKey: key as NSString)
    }
    
    func clearAll() {
        cache.removeAllObjects()
    }
} 