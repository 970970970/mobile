import SwiftUI

struct TermsOfServiceView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Group {
                    Text("用户协议")
                        .font(.title)
                        .padding(.bottom)
                    
                    Text("接受条款")
                        .font(.headline)
                    Text("使用本应用即表示您同意本协议的所有条款。如果您不同意这些条款，请不要使用本应用。")
                    
                    Text("使用许可")
                        .font(.headline)
                    Text("我们授予您个人的、非独占的、不可转让的许可，以使用本应用。您不得：\n• 反向工程或修改应用\n• 将应用用于任何非法目的\n• 干扰应用的正常运行")
                    
                    Text("用户行为")
                        .font(.headline)
                    Text("您同意：\n• 提供准确的信息\n• 遵守所有适用法律\n• 不滥用应用功能\n• 不侵犯他人权利")
                }
                
                Group {
                    Text("内容和数据")
                        .font(.headline)
                    Text("• 应用中的数据来源于公开信息和用户贡献\n• 我们不保证数据的完整性和准确性\n• 用户贡献的内容版权归用户所有")
                    
                    Text("免责声明")
                        .font(.headline)
                    Text("本应用按\'现状\'提供，我们不对以下情况承担责任：\n• 使用或无法使用本应用造成的损失\n• 数据的准确性或完整性\n• 第三方的行为或内容")
                    
                    Text("协议修改")
                        .font(.headline)
                    Text("我们保留随时修改本协议的权利。修改后的协议将在应用内公布。")
                    
                    Text("终止")
                        .font(.headline)
                    Text("我们保留因任何原因终止您使用本应用的权利，无需事先通知。")
                }
                
                Group {
                    Text("适用法律")
                        .font(.headline)
                    Text("本协议受中华人民共和国法律管辖。")
                    
                    Text("联系方式")
                        .font(.headline)
                    Text("如有任何问题，请通过应用内的反馈功能联系我们。")
                }
            }
            .padding()
        }
        .navigationTitle("用户协议")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    NavigationView {
        TermsOfServiceView()
    }
} 