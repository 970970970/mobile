import SwiftUI

struct FeedbackFormView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var feedbackType = FeedbackType.bug
    @State private var content = ""
    @State private var contact = ""
    @State private var showingImagePicker = false
    @State private var selectedImage: UIImage?
    @State private var isSubmitting = false
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    enum FeedbackType: String, CaseIterable {
        case bug = "问题反馈"
        case feature = "功能建议"
        case data = "数据纠错"
        case other = "其他"
    }
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("反馈类型")) {
                    Picker("类型", selection: $feedbackType) {
                        ForEach(FeedbackType.allCases, id: \.self) { type in
                            Text(type.rawValue).tag(type)
                        }
                    }
                    .pickerStyle(.segmented)
                }
                
                Section(header: Text("反馈内容")) {
                    TextEditor(text: $content)
                        .frame(minHeight: 100)
                }
                
                Section(header: Text("联系方式（选填）")) {
                    TextField("邮箱或其他联系方式", text: $contact)
                }
                
                Section(header: Text("附件")) {
                    if let image = selectedImage {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFit()
                            .frame(height: 200)
                        
                        Button("删除图片", role: .destructive) {
                            selectedImage = nil
                        }
                    }
                    
                    Button(action: { showingImagePicker = true }) {
                        HStack {
                            Image(systemName: "photo")
                            Text("添加图片")
                        }
                    }
                }
            }
            .navigationTitle("提交反馈")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("取消") {
                        dismiss()
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("提交") {
                        submitFeedback()
                    }
                    .disabled(content.isEmpty || isSubmitting)
                }
            }
            .sheet(isPresented: $showingImagePicker) {
                ImagePicker(image: $selectedImage)
            }
            .alert("提示", isPresented: $showingAlert) {
                Button("确定", role: .cancel) {
                    if alertMessage == "提交成功" {
                        dismiss()
                    }
                }
            } message: {
                Text(alertMessage)
            }
            .overlay {
                if isSubmitting {
                    ProgressView()
                        .scaleEffect(1.5)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(Color.black.opacity(0.2))
                }
            }
        }
    }
    
    private func submitFeedback() {
        // TODO: 实现反馈提交
        isSubmitting = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            isSubmitting = false
            alertMessage = "提交成功"
            showingAlert = true
        }
    }
}

#Preview {
    FeedbackFormView()
} 