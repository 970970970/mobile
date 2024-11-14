import SwiftUI

struct BrandGridView: View {
    let brands: [Brand]
    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    var body: some View {
        LazyVGrid(columns: columns, spacing: 16) {
            ForEach(brands) { brand in
                BrandGridItem(brand: brand)
            }
        }
    }
}

struct BrandGridItem: View {
    let brand: Brand
    
    var body: some View {
        VStack(alignment: .leading) {
            if let logoPath = brand.logoPath {
                AsyncImage(url: URL(string: "\(AppConfig.mediaHost)/\(logoPath)")) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                } placeholder: {
                    Color.gray
                }
                .frame(height: 100)
                .cornerRadius(8)
            }
            
            Text(brand.name)
                .font(.subheadline)
                .lineLimit(1)
            
            Text(brand.status)
                .font(.caption)
                .foregroundColor(statusColor(brand.status))
        }
        .padding(8)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(radius: 2)
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