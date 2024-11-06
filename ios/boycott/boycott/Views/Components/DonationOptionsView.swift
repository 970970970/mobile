import SwiftUI

struct DonationOptionsView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var selectedAmount = 0
    @State private var isProcessing = false
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    private let amounts = [
        ("☕️", "一杯咖啡", 10),
        ("🍜", "一碗面", 20),
        ("🍱", "一顿饭", 50),
        ("💝", "自定义", -1)
    ]
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Text("感谢您的支持")
                    .font(.title2)
                    .padding(.top)
                
                Text("您的捐赠将帮助我们持续改进产品，提供更好的服务")
                    .font(.subheadline)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                
                LazyVGrid(columns: [
                    GridItem(.flexible()),
                    GridItem(.flexible())
                ], spacing: 20) {
                    ForEach(0..<amounts.count, id: \.self) { index in
                        DonationButton(
                            emoji: amounts[index].0,
                            title: amounts[index].1,
                            amount: amounts[index].2,
                            isSelected: selectedAmount == index
                        ) {
                            selectedAmount = index
                        }
                    }
                }
                .padding()
                
                if selectedAmount == amounts.count - 1 {
                    TextField("输入金额", value: .constant(0), format: .number)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .keyboardType(.numberPad)
                        .frame(width: 200)
                }
                
                Spacer()
                
                Button(action: processDonation) {
                    if isProcessing {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    } else {
                        Text("确认捐赠")
                            .bold()
                    }
                }
                .disabled(selectedAmount < 0 || isProcessing)
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.accentColor)
                .foregroundColor(.white)
                .cornerRadius(10)
                .padding()
            }
            .navigationTitle("支持我们")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("取消") {
                        dismiss()
                    }
                }
            }
            .alert("提示", isPresented: $showingAlert) {
                Button("确定", role: .cancel) {
                    if alertMessage.contains("成功") {
                        dismiss()
                    }
                }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    private func processDonation() {
        // TODO: 实现捐赠处理
        isProcessing = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            isProcessing = false
            alertMessage = "感谢您的支持！"
            showingAlert = true
        }
    }
}

struct DonationButton: View {
    let emoji: String
    let title: String
    let amount: Int
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 8) {
                Text(emoji)
                    .font(.system(size: 40))
                Text(title)
                    .font(.headline)
                if amount > 0 {
                    Text("¥\(amount)")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(isSelected ? Color.accentColor : Color.gray.opacity(0.3), lineWidth: isSelected ? 2 : 1)
            )
        }
        .foregroundColor(.primary)
    }
}

#Preview {
    DonationOptionsView()
} 