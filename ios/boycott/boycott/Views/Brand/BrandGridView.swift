import SwiftUI

struct BrandGridView: View {
    @ObservedObject var viewModel: BrandListViewModel
    @State private var selectedBrand: Brand?
    let columns = [
        GridItem(.flexible(), spacing: 16),
        GridItem(.flexible(), spacing: 16)
    ]
    
    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: 16) {
                ForEach(viewModel.brands) { brand in
                    BrandGridItem(brand: brand)
                        .onTapGesture {
                            selectedBrand = brand
                        }
                        .onAppear {
                            viewModel.loadMoreIfNeeded(currentItem: brand)
                        }
                }
                
                if viewModel.isLoading {
                    HStack {
                        Spacer()
                        ProgressView()
                        Spacer()
                    }
                    .gridCellColumns(2)
                }
                
                if !viewModel.hasMorePages && !viewModel.brands.isEmpty {
                    Text("home_end_of_list".localized)
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity)
                        .gridCellColumns(2)
                }
            }
            .padding()
        }
        .refreshable {
            await viewModel.refresh()
        }
        .sheet(item: $selectedBrand) { brand in
            BrandDetailView(brand: brand, isPresented: $selectedBrand)
        }
    }
}

struct BrandGridItem: View {
    let brand: Brand
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ZStack(alignment: .topTrailing) {
                // Logo 底层
                if let logoPath = brand.logoPath {
                    AsyncImage(url: URL(string: "\(AppConfig.mediaHost)/\(logoPath)")) { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                    } placeholder: {
                        Color.gray.opacity(0.2)
                    }
                    .frame(height: 140)
                    .frame(maxWidth: .infinity)
                    .background(Color(.systemBackground))
                    .cornerRadius(12)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
                } else {
                    Color.gray.opacity(0.2)
                        .frame(height: 140)
                        .cornerRadius(12)
                }
                
                // 状态标签
                if let status = brand.status,
                   let brandStatus = BrandStatus(rawValue: status) {
                    Text(brandStatus.displayName)
                        .font(.caption)
                        .foregroundColor(.white)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(brandStatus.color)
                        .cornerRadius(4)
                        .padding(8)
                }
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(brand.name)
                    .font(.headline)
                    .foregroundColor(.primary)
                    .lineLimit(1)
                
                if let description = brand.description {
                    Text(description)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .lineLimit(2)
                }
            }
            .padding(.horizontal, 4)
        }
        .padding(8)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(radius: 2)
    }
}

struct StatusIcon: View {
    let status: String
    
    var body: some View {
        Group {
            switch status {
            case "support":
                Image(systemName: "checkmark.circle.fill")
                    .foregroundColor(.green)
            case "avoid":
                Image(systemName: "xmark.circle.fill")
                    .foregroundColor(.red)
            default:
                Image(systemName: "minus.circle.fill")
                    .foregroundColor(.yellow)
            }
        }
        .font(.title2)
    }
}