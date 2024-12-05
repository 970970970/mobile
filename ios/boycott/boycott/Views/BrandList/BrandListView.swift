import SwiftUI
import Down

struct BrandListView: View {
    @StateObject private var viewModel = BrandListViewModel()
    @State private var searchText = ""
    @State private var selectedBrand: Brand?
    @State private var selectedFilter: BrandStatus = .all
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 状态过滤器
                BrandStatusFilterView(selectedFilter: $selectedFilter)
                
                // 主要内容
                BrandListContent(
                    viewModel: viewModel,
                    searchText: $searchText,
                    selectedBrand: $selectedBrand,
                    selectedFilter: selectedFilter
                )
            }
            .navigationTitle("nav_brands".localized)
            .sheet(item: $selectedBrand) { brand in
                BrandDetailView(brand: brand, isPresented: $selectedBrand)
            }
        }
        .onAppear {
            viewModel.loadBrands()
        }
    }
}

struct BrandStatusFilterView: View {
    @Binding var selectedFilter: BrandStatus
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(BrandStatus.allCases) { status in
                    FilterChip(
                        title: status.displayName,
                        isSelected: selectedFilter == status,
                        color: status.color
                    ) {
                        selectedFilter = status
                    }
                }
            }
            .padding(.horizontal)
            .padding(.vertical, 8)
        }
        .background(Color(UIColor.systemBackground))
    }
}

struct FilterChip: View {
    let title: String
    let isSelected: Bool
    let color: Color
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(isSelected ? color : color.opacity(0.1))
                .foregroundColor(isSelected ? .white : color)
                .cornerRadius(20)
        }
    }
}

struct BrandListContent: View {
    @ObservedObject var viewModel: BrandListViewModel
    @Binding var searchText: String
    @Binding var selectedBrand: Brand?
    let selectedFilter: BrandStatus
    
    var filteredBrands: [Brand] {
        viewModel.brands.filter { brand in
            let matchesFilter = selectedFilter == .all || brand.status == selectedFilter.rawValue
            let matchesSearch = searchText.isEmpty || 
                brand.name.localizedCaseInsensitiveContains(searchText)
            return matchesFilter && matchesSearch
        }
    }
    
    var body: some View {
        List {
            if filteredBrands.isEmpty && !viewModel.isLoading {
                Text("brand_no_data".localized)
                    .foregroundColor(.gray)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .listRowSeparator(.hidden)
            } else {
                ForEach(filteredBrands) { brand in
                    BrandRowView(brand: brand)
                        .onTapGesture {
                            selectedBrand = brand
                        }
                        .listRowInsets(EdgeInsets(top: 12, leading: 16, bottom: 12, trailing: 16))
                }
                
                if viewModel.isLoading {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                        .listRowSeparator(.hidden)
                }
            }
        }
        .listStyle(.plain)
        .refreshable {
            viewModel.refresh()
        }
        .searchable(text: $searchText, prompt: "搜索品牌")
    }
}

struct BrandDetailView: View {
    let brand: Brand
    @Binding var isPresented: Brand?
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    // 品牌 Logo
                    if let logoPath = brand.logoPath {
                        AsyncImage(url: URL(string: "\(AppConfig.mediaHost)/\(logoPath)")) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                        } placeholder: {
                            Color.gray
                        }
                        .frame(height: 200)
                        .cornerRadius(12)
                    }
                    
                    // 品牌状态
                    if let status = brand.status {
                        HStack {
                            Text(localizedStatus(status))
                                .font(.headline)
                                .padding(.horizontal, 16)
                                .padding(.vertical, 8)
                                .background(statusColor(status))
                                .foregroundColor(.white)
                                .cornerRadius(20)
                            Spacer()
                        }
                    }
                    
                    // 品牌描述
                    VStack(alignment: .leading, spacing: 12) {
                        Text("brand_about".localized)
                            .font(.headline)
                        
                        if let description = brand.description {
                            MarkdownTextView(markdown: description)
                                .frame(minHeight: 100)
                                .background(
                                    RoundedRectangle(cornerRadius: 12)
                                        .fill(colorScheme == .dark ? Color(.systemGray6) : Color(.systemGray6))
                                )
                        }
                    }
                }
                .padding()
            }
            .navigationTitle(brand.name)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { isPresented = nil }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray)
                            .imageScale(.large)
                    }
                }
            }
        }
    }
    
    private func localizedStatus(_ status: String) -> String {
        switch status.lowercased() {
        case "support":
            return "status_support".localized
        case "avoid":
            return "status_avoid".localized
        default:
            return "status_neutral".localized
        }
    }
    
    private func statusColor(_ status: String) -> Color {
        switch status.lowercased() {
        case "support":
            return .green
        case "avoid":
            return .red
        default:
            return .yellow
        }
    }
}

enum BrandStatus: String, CaseIterable, Identifiable {
    case all = "all"
    case support = "support"
    case avoid = "avoid"
    case neutral = "neutral"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .all: return "brand_filter_all".localized
        case .support: return "status_support".localized
        case .avoid: return "status_avoid".localized
        case .neutral: return "status_neutral".localized
        }
    }
    
    var color: Color {
        switch self {
        case .all: return .blue
        case .support: return .green
        case .avoid: return .red
        case .neutral: return .yellow
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
        HStack(spacing: 12) {
            // Logo
            if let logoPath = brand.logoPath {
                AsyncImage(url: URL(string: "\(AppConfig.mediaHost)/\(logoPath)")) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                } placeholder: {
                    Color.gray
                }
                .frame(width: 60, height: 60)
                .cornerRadius(8)
            } else {
                Color.gray
                    .frame(width: 60, height: 60)
                    .cornerRadius(8)
            }
            
            // 品牌信息
            VStack(alignment: .leading, spacing: 4) {
                Text(brand.name)
                    .font(.headline)
                
                if let description = brand.description {
                    Text(description)
                        .font(.subheadline)
                        .foregroundColor(.gray)
                        .lineLimit(2)
                }
            }
            
            Spacer()
            
            // 状态标签
            if let status = brand.status {
                BrandStatusBadge(status: status)
            }
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

// 添加状态标签组件
struct BrandStatusBadge: View {
    let status: String
    
    var body: some View {
        Text(localizedStatus)
            .font(.caption)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(statusColor)
            .foregroundColor(.white)
            .cornerRadius(12)
    }
    
    private var localizedStatus: String {
        switch status.lowercased() {
        case "support":
            return "status_support".localized
        case "avoid":
            return "status_avoid".localized
        default:
            return "status_neutral".localized
        }
    }
    
    private var statusColor: Color {
        switch status.lowercased() {
        case "support":
            return .green
        case "avoid":
            return .red
        default:
            return .yellow
        }
    }
}