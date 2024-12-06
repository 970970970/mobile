import SwiftUI

struct SearchHistoryView: View {
    @Environment(\.dismiss) var dismiss
    @Binding var searchText: String
    @Binding var isPresented: Bool
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
                        Text("search_no_history".localized)
                            .foregroundColor(.gray)
                            .frame(maxWidth: .infinity, alignment: .center)
                            .listRowSeparator(.hidden)
                    } else {
                        ForEach(viewModel.recentSearches, id: \.self) { search in
                            Button(action: {
                                searchText = search
                                performSearch(search)
                            }) {
                                HStack {
                                    Image(systemName: "clock")
                                        .foregroundColor(.gray)
                                    Text(search)
                                        .foregroundColor(.primary)
                                    Spacer()
                                }
                            }
                        }
                        .onDelete(perform: viewModel.deleteSearchHistory)
                    }
                }
                .listStyle(.plain)
            }
            .navigationTitle("nav_search".localized)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("button_cancel".localized) {
                        isPresented = false
                    }
                }
                
                if !viewModel.recentSearches.isEmpty {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("search_clear_history".localized) {
                            viewModel.clearSearchHistory()
                        }
                    }
                }
            }
        }
        .onAppear {
            viewModel.loadSearchHistory()
        }
    }
    
    private func performSearch(_ query: String) {
        if !query.isEmpty {
            viewModel.addSearch(query)
            searchText = query
            isPresented = false
            
            // 发送搜索通知
            NotificationCenter.default.post(name: .performSearch, object: query)
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
                TextField("search_brand".localized, text: $text)
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
                Button("search".localized, action: onSearch)
                    .foregroundColor(.blue)
            }
        }
    }
}

#if DEBUG
struct SearchHistoryView_Previews: PreviewProvider {
    static var previews: some View {
        SearchHistoryView(searchText: .constant(""), isPresented: .constant(true))
    }
}
#endif
