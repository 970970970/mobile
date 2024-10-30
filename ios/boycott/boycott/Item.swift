//
//  Item.swift
//  boycott
//
//  Created by 马思奇 on 2024/10/30.
//

import Foundation
import SwiftData

@Model
final class Item {
    var timestamp: Date
    
    init(timestamp: Date) {
        self.timestamp = timestamp
    }
}
