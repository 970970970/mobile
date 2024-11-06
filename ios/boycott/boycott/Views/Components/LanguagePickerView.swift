import SwiftUI

struct LanguagePickerView: View {
    @Binding var selectedLanguage: Language?
    let languages: [Language]
    let onDismiss: () -> Void
    
    var body: some View {
        let content = List(languages, id: \.code) { language in
            languageRow(language)
        }
        
        NavigationView {
            content
                .navigationTitle("select_language".localized)
                .navigationBarItems(trailing: dismissButton)
        }
    }
    
    private func languageRow(_ language: Language) -> some View {
        Button(action: {
            selectedLanguage = language
            onDismiss()
        }) {
            HStack {
                Text(language.flag)
                Text(language.name)
                Spacer()
                if language.code == selectedLanguage?.code {
                    Image(systemName: "checkmark")
                        .foregroundColor(.blue)
                }
            }
        }
    }
    
    private var dismissButton: some View {
        Button("done".localized) {
            onDismiss()
        }
    }
}

struct LanguagePickerView_Previews: PreviewProvider {
    static var previews: some View {
        LanguagePickerView(
            selectedLanguage: .constant(Language(
                id: 1,
                code: "en-US",
                name: "English",
                flag: "🇺🇸",
                status: 1
            )),
            languages: [
                Language(
                    id: 1,
                    code: "en-US",
                    name: "English",
                    flag: "🇺🇸",
                    status: 1
                ),
                Language(
                    id: 2,
                    code: "zh-CN",
                    name: "Chinese",
                    flag: "🇨🇳",
                    status: 1
                )
            ],
            onDismiss: {}
        )
    }
} 