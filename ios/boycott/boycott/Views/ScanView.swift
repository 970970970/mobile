import SwiftUI
import AVFoundation
import Photos

struct ScanView: View {
    @StateObject private var scannerViewModel = ScannerViewModel()
    @State private var showingImagePicker = false
    @State private var selectedImage: UIImage?
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        NavigationView {
            ZStack {
                // 相机预览
                if scannerViewModel.session.isRunning {
                    CameraPreview(session: scannerViewModel.session)
                        .ignoresSafeArea()
                } else {
                    Color.black
                        .ignoresSafeArea()
                        .overlay(
                            Text("camera_not_enabled".localized)
                                .foregroundColor(.white)
                        )
                }
                
                VStack {
                    Spacer()
                    
                    // 控制按钮
                    HStack(spacing: 60) {
                        Button(action: {
                            // 检查相册权限
                            PHPhotoLibrary.requestAuthorization { status in
                                DispatchQueue.main.async {
                                    if status == .authorized {
                                        showingImagePicker = true
                                    } else {
                                        alertMessage = "photo_library_permission_required".localized
                                        showingAlert = true
                                    }
                                }
                            }
                        }) {
                            Image(systemName: "photo")
                                .font(.system(size: 30))
                                .foregroundColor(.white)
                        }
                        
                        Button(action: {
                            // 检查相机权限
                            AVCaptureDevice.requestAccess(for: .video) { granted in
                                if granted {
                                    scannerViewModel.setupCamera()
                                } else {
                                    DispatchQueue.main.async {
                                        alertMessage = "camera_permission_required".localized
                                        showingAlert = true
                                    }
                                }
                            }
                        }) {
                            Image(systemName: "camera")
                                .font(.system(size: 30))
                                .foregroundColor(.white)
                        }
                    }
                    .padding(.bottom, 50)
                }
            }
            .navigationTitle("scan".localized)
            .navigationBarTitleDisplayMode(.inline)
            .sheet(isPresented: $showingImagePicker) {
                ImagePicker(image: $selectedImage)
            }
            .alert("alert".localized, isPresented: $showingAlert) {
                Button("ok".localized, role: .cancel) { }
            } message: {
                Text(alertMessage)
            }
            .onAppear {
                // 检查相机权限
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    if granted {
                        scannerViewModel.setupCamera()
                    }
                }
            }
        }
    }
}

struct ScanResultOverlay: View {
    let result: ScanResult
    
    var body: some View {
        VStack {
            Spacer()
            
            VStack(spacing: 20) {
                // 状态指示
                Circle()
                    .fill(result.isInBoycottList ? Color.red : Color.green)
                    .frame(width: 60, height: 60)
                    .overlay(
                        Image(systemName: result.isInBoycottList ? "xmark" : "checkmark")
                            .font(.system(size: 30, weight: .bold))
                            .foregroundColor(.white)
                    )
                
                // 公司信息
                Text(result.companyName)
                    .font(.title2)
                    .bold()
                
                Text(result.description)
                    .font(.body)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                
                // 替代产品推荐
                if result.isInBoycottList, let alternatives = result.alternatives {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 15) {
                            ForEach(alternatives) { product in
                                AlternativeProductCard(product: product)
                            }
                        }
                        .padding(.horizontal)
                    }
                }
            }
            .padding()
            .background(Color(UIColor.systemBackground))
            .cornerRadius(20)
            .shadow(radius: 10)
            .padding()
        }
    }
}

struct AlternativeProductCard: View {
    let product: AlternativeProduct
    
    var body: some View {
        VStack(alignment: .leading) {
            if let imageUrl = product.imageUrl {
                AsyncImage(url: imageUrl) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: 120, height: 120)
                        .clipped()
                        .cornerRadius(10)
                } placeholder: {
                    Rectangle()
                        .fill(Color.gray.opacity(0.2))
                        .frame(width: 120, height: 120)
                        .cornerRadius(10)
                }
            }
            
            Text(product.name)
                .font(.headline)
                .lineLimit(1)
            
            Text(product.price)
                .font(.subheadline)
                .foregroundColor(.gray)
            
            HStack {
                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
                Text(String(format: "%.1f", product.rating))
                    .font(.caption)
            }
        }
        .frame(width: 120)
    }
}

#Preview {
    ScanView()
}
