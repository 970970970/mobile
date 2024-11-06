import SwiftUI

struct SettingRow: View {
    let title: String
    let icon: String
    
    var body: some View {
        HStack {
            Image(systemName: icon)
            Text(title)
        }
    }
}

#Preview {
    SettingRow(title: "设置项", icon: "gear")
} 