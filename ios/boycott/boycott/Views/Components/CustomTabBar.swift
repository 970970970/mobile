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
            
            // 中间的空隙，为凸起的扫描按钮留出空间
            Color.clear
                .frame(width: 60)  // 减小空隙宽度
            
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
        .padding(.horizontal, 16)  // 减小水平边距
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
            .offset(y: -24),  // 减小凸出高度
            alignment: .center
        )
    }
}

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

struct ScanButton: View {
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            ZStack {
                Circle()
                    .fill(Color.blue)
                    .frame(width: 56, height: 56)  // 减小按钮尺寸
                    .shadow(radius: 4)
                
                VStack(spacing: 2) {
                    Image(systemName: "camera.viewfinder")
                        .font(.system(size: 24))  // 减小图标尺寸
                    Text("扫描")
                        .font(.caption)
                }
                .foregroundColor(.white)
            }
        }
    }
} 