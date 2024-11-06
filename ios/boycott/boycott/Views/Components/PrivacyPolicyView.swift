import SwiftUI

struct PrivacyPolicyView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Group {
                    Text("隐私政策")
                        .font(.title)
                        .padding(.bottom)
                    
                    Text("信息收集")
                        .font(.headline)
                    Text("我们只收集必要的信息来提供服务，包括：\n• 设备信息\n• 使用数据\n• 扫描历史（仅保存在本地）")
                    
                    Text("信息使用")
                        .font(.headline)
                    Text("收集的信息仅用于：\n• 提供和改进服务\n• 分析使用情况\n• 处理反馈")
                    
                    Text("信息共享")
                        .font(.headline)
                    Text("我们不会出售或共享您的个人信息。只有在以下情况下才会共享信息：\n• 经您同意\n• 法律要求")
                }
                
                Group {
                    Text("数据安全")
                        .font(.headline)
                    Text("我们采用行业标准的安全措施保护您的信息。")
                    
                    Text("用户权利")
                        .font(.headline)
                    Text("您有权：\n• 访问您的数据\n• 更正不准确的信息\n• 删除您的数据\n• 退出数据收集")
                    
                    Text("政策更新")
                        .font(.headline)
                    Text("我们可能会更新本隐私政策。重大变更时会通知您。")
                    
                    Text("联系我们")
                        .font(.headline)
                    Text("如有任何问题，请通过应用内的反馈功能联系我们。")
                }
            }
            .padding()
        }
        .navigationTitle("隐私政策")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    NavigationView {
        PrivacyPolicyView()
    }
} 