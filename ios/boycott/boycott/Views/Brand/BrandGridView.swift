import SwiftUI

struct BrandGridView: View {
    let brands: [Brand]
    @State private var selectedBrand: Brand?
    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    var body: some View {
        LazyVGrid(columns: columns, spacing: 16) {
            ForEach(brands) { brand in
                BrandGridItem(brand: brand)
                    .onTapGesture {
                        selectedBrand = brand
                    }
            }
        }
        .sheet(item: $selectedBrand) { brand in
            BrandDetailView(brand: brand, isPresented: $selectedBrand)
        }
    }
}

struct BrandGridItem: View {
    let brand: Brand
    
    var body: some View {
        VStack(alignment: .leading) {
            ZStack(alignment: .topTrailing) {
                // Logo 底层
                if let logoPath = brand.logoPath {
                    AsyncImage(url: URL(string: "\(AppConfig.mediaHost)/\(logoPath)")) { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                    } placeholder: {
                        Color.gray
                    }
                    .frame(height: 120)
                    .frame(maxWidth: .infinity)
                    .background(Color(.systemGray6))
                    .cornerRadius(8)
                } else {
                    Color.gray
                        .frame(height: 120)
                        .cornerRadius(8)
                }
                
                // 状态图标覆盖层
                if brand.status == "avoid" {
                    Image("avoid-overlay")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 60)
                        .padding(8)
                        .zIndex(1)
                }
            }
            
            Text(brand.name)
                .font(.subheadline)
                .lineLimit(1)
                .foregroundColor(.primary)
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