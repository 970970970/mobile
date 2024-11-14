import SwiftUI

struct SearchHistoryView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject private var viewModel = SearchHistoryViewModel()
    
    var body: some View {
        NavigationView {
            List {
                if viewModel.recentSearches.isEmpty {
                    Text("暂无搜索历史")
                        .foregroundColor(.gray)
                } else {
                    Section(header: Text("最近搜索")) {
                        ForEach(viewModel.recentSearches, id: \.self) { search in
                            Button(action: { performSearch(search) }) {
                                HStack {
                                    Image(systemName: "clock")
                                        .foregroundColor(.gray)
                                    Text(search)
                                    Spacer()
                                }
                            }
                        }
                    }
                    
                    Button(action: viewModel.clearHistory) {
                        Text("清除搜索历史")
                            .foregroundColor(.red)
                    }
                }
            }
            .navigationTitle("搜索历史")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("完成") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    private func performSearch(_ query: String) {
        // TODO: 实现搜索功能
        dismiss()
    }
}

#if DEBUG
struct SearchHistoryView_Previews: PreviewProvider {
    static var previews: some View {
        SearchHistoryView()
    }
}
#endif 
