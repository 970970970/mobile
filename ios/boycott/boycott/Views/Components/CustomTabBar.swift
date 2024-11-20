import SwiftUI

struct CustomTabBar: View {
    @Binding var selectedTab: Int
    
    var body: some View {
        HStack {
            // 首页
            TabBarButton(
                image: "house.fill",
                text: "首页",
                isSelected: selectedTab == 0
            ) {
                selectedTab = 0
            }
            .frame(maxWidth: .infinity)
            
            // 品牌
            TabBarButton(
                image: "tag.fill",
                text: "品牌",
                isSelected: selectedTab == 1
            ) {
                selectedTab = 1
            }
            .frame(maxWidth: .infinity)
            
            // 中间的扫描按钮占位
            Color.clear
                .frame(width: 80)
            
            // 文章
            TabBarButton(
                image: "newspaper.fill",
                text: "文章",
                isSelected: selectedTab == 3
            ) {
                selectedTab = 3
            }
            .frame(maxWidth: .infinity)
            
            // 设置
            TabBarButton(
                image: "gearshape.fill",
                text: "设置",
                isSelected: selectedTab == 4
            ) {
                selectedTab = 4
            }
            .frame(maxWidth: .infinity)
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .background(
            Color(.systemBackground)
                .shadow(radius: 2)
        )
        .overlay(
            // 凸起的扫描按钮
            ScanButton {
                selectedTab = 2
            }
            .offset(y: -32),
            alignment: .center
        )
    }
}

// 普通标签按钮组件
struct TabBarButton: View {
    let image: String
    let text: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Image(systemName: image)
                    .font(.system(size: isSelected ? 24 : 20))
                Text(text)
                    .font(.caption)
            }
            .foregroundColor(isSelected ? .blue : .gray)
        }
    }
}

// 扫描按钮组件
struct ScanButton: View {
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            ZStack {
                Circle()
                    .fill(Color.blue)
                    .frame(width: 64, height: 64)
                    .shadow(radius: 4)
                
                VStack(spacing: 2) {
                    Image(systemName: "camera.viewfinder")
                        .font(.system(size: 28))
                    Text("扫描")
                        .font(.caption)
                }
                .foregroundColor(.white)
            }
        }
    }
} 