import SwiftUI
import AVFoundation

struct CameraPreview: UIViewRepresentable {
    let session: AVCaptureSession
    
    func makeUIView(context: Context) -> UIView {
        let view = UIView(frame: UIScreen.main.bounds)
        
        let previewLayer = AVCaptureVideoPreviewLayer(session: session)
        previewLayer.frame = view.frame
        previewLayer.videoGravity = .resizeAspectFill
        view.layer.addSublayer(previewLayer)
        
        // 添加扫描框
        let scanFrame = UIView(frame: CGRect(x: 50, y: 100, width: view.frame.width - 100, height: view.frame.width - 100))
        scanFrame.layer.borderColor = UIColor.white.cgColor
        scanFrame.layer.borderWidth = 2
        view.addSubview(scanFrame)
        
        return view
    }
    
    func updateUIView(_ uiView: UIView, context: Context) {
        // 不需要更新视图
    }
}
