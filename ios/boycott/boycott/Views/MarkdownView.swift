import SwiftUI
import Down

struct MarkdownView: View {
    let markdown: String
    
    var body: some View {
        ScrollView {
            DownViewRepresentable(markdown: markdown)
                .frame(maxWidth: .infinity, minHeight: 100)
        }
    }
}

// 分离 UIViewRepresentable 实现
struct DownViewRepresentable: UIViewRepresentable {
    let markdown: String
    
    func makeUIView(context: Context) -> UITextView {
        let textView = UITextView()
        textView.isEditable = false
        textView.isSelectable = true
        textView.backgroundColor = .clear
        textView.textContainerInset = UIEdgeInsets(top: 16, left: 16, bottom: 16, right: 16)
        textView.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        textView.setContentCompressionResistancePriority(.defaultLow, for: .vertical)
        textView.isScrollEnabled = false
        
        // 根据当前语言设置文本对齐方式
        let isRTL = isRTLLanguage(LanguageManager.shared.currentLanguage)
        textView.textAlignment = isRTL ? .right : .left
        
        // 配置 Down
        let down = Down(markdownString: markdown)
        
        do {
            let attributedString = try down.toAttributedString()
            // 设置段落样式
            let mutableAttrString = NSMutableAttributedString(attributedString: attributedString)
            mutableAttrString.enumerateAttribute(.paragraphStyle, in: NSRange(location: 0, length: mutableAttrString.length), options: []) { (value, range, stop) in
                if let paragraphStyle = value as? NSMutableParagraphStyle {
                    paragraphStyle.alignment = isRTL ? .right : .left
                    mutableAttrString.addAttribute(.paragraphStyle, value: paragraphStyle, range: range)
                }
            }
            textView.attributedText = mutableAttrString
            
            // 调整大小以适应内容
            textView.sizeToFit()
            textView.layoutIfNeeded()
        } catch {
            textView.text = markdown
            print("Markdown rendering error:", error)
        }
        
        return textView
    }
    
    func updateUIView(_ uiView: UITextView, context: Context) {
        do {
            let down = Down(markdownString: markdown)
            let attributedString = try down.toAttributedString()
            // 更新时也设置段落样式
            let mutableAttrString = NSMutableAttributedString(attributedString: attributedString)
            let isRTL = isRTLLanguage(LanguageManager.shared.currentLanguage)
            mutableAttrString.enumerateAttribute(.paragraphStyle, in: NSRange(location: 0, length: mutableAttrString.length), options: []) { (value, range, stop) in
                if let paragraphStyle = value as? NSMutableParagraphStyle {
                    paragraphStyle.alignment = isRTL ? .right : .left
                    mutableAttrString.addAttribute(.paragraphStyle, value: paragraphStyle, range: range)
                }
            }
            uiView.attributedText = mutableAttrString
            uiView.textAlignment = isRTL ? .right : .left
            
            // 调整大小以适应内容
            uiView.sizeToFit()
            uiView.layoutIfNeeded()
        } catch {
            uiView.text = markdown
            print("Markdown rendering error:", error)
        }
    }
    
    // 判断是否是从右向左的语言
    private func isRTLLanguage(_ languageCode: String) -> Bool {
        let rtlLanguages = ["ar", "fa", "he", "ur"]
        return rtlLanguages.contains(languageCode)
    }
}

#Preview {
    MarkdownView(markdown: """
    ### 标题
    
    这是一段测试文本
    
    ## 子标题
    - 列表项1
    - 列表项2
    
    **加粗文本**
    
    > 引用文本
    """)
} 