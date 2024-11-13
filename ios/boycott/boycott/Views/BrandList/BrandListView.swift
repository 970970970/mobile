import SwiftUI
import Down

struct BrandListView: View {
    @StateObject private var viewModel = BrandListViewModel()
    @State private var searchText = ""
    @State private var selectedBrand: Brand?
    
    var body: some View {
        NavigationView {
            BrandListContent(
                viewModel: viewModel,
                searchText: $searchText,
                selectedBrand: $selectedBrand
            )
            .navigationTitle("品牌列表")
            .sheet(item: $selectedBrand) { brand in
                BrandDetailView(brand: brand, isPresented: $selectedBrand)
            }
        }
        .onAppear {
            print("🚀 [BrandListView] View appeared")
            viewModel.loadBrands()
        }
    }
}

struct BrandListContent: View {
    @ObservedObject var viewModel: BrandListViewModel
    @Binding var searchText: String
    @Binding var selectedBrand: Brand?
    
    var body: some View {
        VStack {
            if viewModel.brands.isEmpty && !viewModel.isLoading {
                Text("暂无数据")
                    .foregroundColor(.gray)
            } else {
                BrandListScrollView(viewModel: viewModel, selectedBrand: $selectedBrand)
            }
        }
        .searchable(text: $searchText)
        .onChange(of: searchText) { newValue in
            print("🔍 [BrandListView] Search text changed: \(newValue)")
            viewModel.search(keyword: newValue)
        }
        .refreshable {
            print("🔄 [BrandListView] Pull to refresh triggered")
            viewModel.refresh()
        }
    }
}

struct BrandListScrollView: View {
    @ObservedObject var viewModel: BrandListViewModel
    @Binding var selectedBrand: Brand?
    
    var body: some View {
        List {
            ForEach(viewModel.brands, id: \.id) { brand in
                BrandRowView(brand: brand)
                    .onTapGesture {
                        selectedBrand = brand
                    }
                    .onAppear {
                        if brand.id == viewModel.brands.last?.id {
                            viewModel.loadMoreIfNeeded()
                        }
                    }
            }
            
            if viewModel.isLoading {
                ProgressView()
                    .frame(maxWidth: .infinity)
            }
        }
    }
}

struct BrandDetailView: View {
    let brand: Brand
    @Binding var isPresented: Brand?
    
    var body: some View {
        NavigationView {
            VStack(alignment: .leading, spacing: 16) {
                Text(brand.name)
                    .font(.largeTitle)
                    .bold()
                
                if let logoPath = brand.logoPath {
                    AsyncImage(url: URL(string: "\(AppConfig.mediaHost)/\(logoPath)")) { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                    } placeholder: {
                        Color.gray
                    }
                    .frame(height: 200)
                    .cornerRadius(8)
                }
                
                Text("状态: \(brand.status)")
                    .font(.headline)
                    .foregroundColor(statusColor(brand.status))
                
                Divider()
                
                ScrollView {
                    MarkdownTextView(markdown: brand.description)
                        .frame(minHeight: 100)
                        .padding()
                }
            }
            .padding()
            .navigationBarItems(trailing: Button(action: {
                isPresented = nil
            }) {
                Image(systemName: "xmark")
                    .foregroundColor(.primary)
            })
        }
    }
    
    private func statusColor(_ status: String) -> Color {
        switch status {
        case "support":
            return .green
        case "avoid":
            return .red
        default:
            return .yellow
        }
    }
}

struct MarkdownTextView: UIViewRepresentable {
    let markdown: String
    
    func makeUIView(context: Context) -> UITextView {
        let textView = UITextView()
        textView.isEditable = false
        textView.isScrollEnabled = true
        textView.backgroundColor = .clear
        textView.dataDetectorTypes = [.link]
        textView.linkTextAttributes = [
            .foregroundColor: UIColor.blue,
            .underlineStyle: NSUnderlineStyle.single.rawValue
        ]
        return textView
    }
    
    func updateUIView(_ uiView: UITextView, context: Context) {
        if let attributedString = try? Down(markdownString: markdown).toAttributedString() {
            uiView.attributedText = attributedString
        } else {
            uiView.text = markdown
        }
    }
}

struct BrandRowView: View {
    let brand: Brand
    
    var body: some View {
        HStack {
            BrandLogoView(logoPath: brand.logoPath)
            BrandInfoView(name: brand.name, status: brand.status)
        }
    }
}

struct BrandLogoView: View {
    let logoPath: String?
    
    var body: some View {
        if let path = logoPath {
            AsyncImage(url: URL(string: "\(AppConfig.mediaHost)/\(path)")) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fit)
            } placeholder: {
                Color.gray
            }
            .frame(width: 50, height: 50)
            .cornerRadius(8)
        } else {
            Color.gray
                .frame(width: 50, height: 50)
                .cornerRadius(8)
        }
    }
}

struct BrandInfoView: View {
    let name: String
    let status: String
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(name)
                .font(.headline)
            Text(status)
                .font(.subheadline)
                .foregroundColor(statusColor)
        }
    }
    
    private var statusColor: Color {
        switch status {
        case "support":
            return .green
        case "avoid":
            return .red
        default:
            return .yellow
        }
    }
}