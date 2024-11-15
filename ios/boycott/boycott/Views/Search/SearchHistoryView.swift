import SwiftUI

struct SearchHistoryView: View {
    @Environment(\.dismiss) var dismiss
    @State private var searchText = ""
    @StateObject private var viewModel = SearchHistoryViewModel()
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 搜索框
                SearchBar(text: $searchText) {
                    // 执行搜索
                    performSearch(searchText)
                }
                .padding(.horizontal)
                .padding(.vertical, 8)
                
                List {
                    if viewModel.recentSearches.isEmpty {
                        Text("暂无搜索历史")
                            .foregroundColor(.gray)
                            .frame(maxWidth: .infinity, alignment: .center)
                            .listRowSeparator(.hidden)
                    } else {
                        Section(header: Text("最近搜索")) {
                            ForEach(viewModel.recentSearches, id: \.self) { search in
                                Button(action: { performSearch(search) }) {
                                    HStack {
                                        Image(systemName: "clock")
                                            .foregroundColor(.gray)
                                        Text(search)
                                            .foregroundColor(.primary)
                                        Spacer()
                                    }
                                }
                            }
                        }
                        
                        if !viewModel.recentSearches.isEmpty {
                            Button(action: viewModel.clearHistory) {
                                Text("清除搜索历史")
                                    .foregroundColor(.red)
                            }
                        }
                    }
                }
            }
            .navigationTitle("搜索")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("取消") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    private func performSearch(_ query: String) {
        guard !query.isEmpty else { return }
        
        // 保存搜索历史
        SearchHistoryManager.shared.addSearch(query)
        
        // 调用搜索接口
        APIService.shared.searchBrands(keyword: query) { result in
            switch result {
            case .success(let response):
                if response.total == 1, let brand = response.items.first {
                    // 如果只有一个结果，显示品牌详情
                    NotificationCenter.default.post(
                        name: .showBrandDetail,
                        object: brand
                    )
                } else if response.total > 1 {
                    // 如果有多个结果，切换到列表页并显示搜索结果
                    NotificationCenter.default.post(name: .switchToTab, object: 1)
                    // 稍微延迟一下发送搜索通知，确保标签切换完成
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                        NotificationCenter.default.post(
                            name: .performSearch,
                            object: query
                        )
                    }
                }
                dismiss()
            case .failure(let error):
                print("Search error:", error)
                // TODO: 显示错误提示
            }
        }
    }
}

struct SearchBar: View {
    @Binding var text: String
    let onSearch: () -> Void
    
    var body: some View {
        HStack {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)
                TextField("搜索品牌", text: $text)
                    .submitLabel(.search)
                    .onSubmit(onSearch)
                    .autocapitalization(.none)
                
                if !text.isEmpty {
                    Button(action: { text = "" }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray)
                    }
                }
            }
            .padding(8)
            .background(Color(.systemGray6))
            .cornerRadius(10)
            
            if !text.isEmpty {
                Button("搜索", action: onSearch)
                    .foregroundColor(.blue)
            }
        }
    }
}

#if DEBUG
struct SearchHistoryView_Previews: PreviewProvider {
    static var previews: some View {
        SearchHistoryView()
    }
}
#endif 
