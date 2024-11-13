import AVFoundation
import UIKit

class ScannerViewModel: NSObject, ObservableObject, AVCaptureMetadataOutputObjectsDelegate {
    @Published var session = AVCaptureSession()
    @Published var isTorchOn = false
    @Published var scanResult: ScanResult?
    @Published var isProcessing = false
    @Published var isCameraReady = false
    @Published var errorMessage: String?
    @Published var isAuthorized = false
    
    private let captureDevice = AVCaptureDevice.default(for: .video)
    
    override init() {
        super.init()
        checkPermission()
        setupCamera()
    }
    
    func checkPermission() {
        print("📸 [Camera] Checking camera permission...")
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            print("✅ [Camera] Permission authorized")
            setupCamera()
            isAuthorized = true
        case .notDetermined:
            print("⏳ [Camera] Permission not determined, requesting...")
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                if granted {
                    print("✅ [Camera] Permission granted")
                    DispatchQueue.main.async {
                        self?.setupCamera()
                        self?.isAuthorized = true
                    }
                } else {
                    print("❌ [Camera] Permission denied by user")
                    DispatchQueue.main.async {
                        self?.isAuthorized = false
                    }
                }
            }
        case .denied:
            print("❌ [Camera] Permission denied")
            isAuthorized = false
        case .restricted:
            print("⚠️ [Camera] Permission restricted")
            isAuthorized = false
        @unknown default:
            print("❓ [Camera] Unknown permission status")
            isAuthorized = false
        }
    }
    
    func setupCamera() {
        guard let device = captureDevice else { 
            print("❌ [Camera] No camera device available")
            DispatchQueue.main.async {
                self.errorMessage = "camera_not_available".localized
            }
            return 
        }
        
        print("🎥 [Camera] Setting up camera...")
        
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            guard let self = self else { return }
            
            if self.session.isRunning {
                print("⚠️ [Camera] Session already running, stopping...")
                self.session.stopRunning()
            }
            
            print("⚙️ [Camera] Configuring session...")
            self.session.beginConfiguration()
            
            // 清理现有配置
            self.session.inputs.forEach { self.session.removeInput($0) }
            self.session.outputs.forEach { self.session.removeOutput($0) }
            
            do {
                let input = try AVCaptureDeviceInput(device: device)
                if self.session.canAddInput(input) {
                    self.session.addInput(input)
                    print("✅ [Camera] Added camera input")
                } else {
                    print("❌ [Camera] Cannot add camera input")
                }
                
                let output = AVCaptureMetadataOutput()
                if self.session.canAddOutput(output) {
                    self.session.addOutput(output)
                    output.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
                    output.metadataObjectTypes = [.qr, .ean13, .ean8, .code128]
                    print("✅ [Camera] Added metadata output")
                } else {
                    print("❌ [Camera] Cannot add metadata output")
                }
                
                self.session.commitConfiguration()
                print("✅ [Camera] Session configured")
                
                DispatchQueue.main.async {
                    self.isCameraReady = true
                    if !self.session.isRunning {
                        print("▶️ [Camera] Starting session...")
                        self.session.startRunning()
                    }
                }
            } catch {
                print("❌ [Camera] Setup error: \(error)")
                DispatchQueue.main.async {
                    self.isCameraReady = false
                    self.errorMessage = String(format: "camera_init_failed".localized, error.localizedDescription)
                }
            }
        }
    }
    
    func toggleTorch() {
        guard let device = captureDevice, device.hasTorch else { 
            DispatchQueue.main.async {
                self.errorMessage = "torch_not_supported".localized
            }
            return 
        }
        
        do {
            try device.lockForConfiguration()
            device.torchMode = isTorchOn ? .off : .on
            DispatchQueue.main.async {
                self.isTorchOn.toggle()
            }
            device.unlockForConfiguration()
        } catch {
            DispatchQueue.main.async {
                self.errorMessage = "torch_control_failed".localized
            }
            print("Torch error: \(error)")
        }
    }
    
    func processImage(_ image: UIImage) {
        guard !isProcessing else { return }
        DispatchQueue.main.async {
            self.isProcessing = true
        }
        
        APIService.shared.recognizeImage(image) { [weak self] result in
            DispatchQueue.main.async {
                self?.isProcessing = false
                switch result {
                case .success(let scanResult):
                    self?.scanResult = scanResult
                case .failure(let error):
                    self?.errorMessage = String(format: "image_recognition_failed".localized, error.localizedDescription)
                    print("Image recognition error: \(error)")
                }
            }
        }
    }
    
    func metadataOutput(_ output: AVCaptureMetadataOutput, 
                       didOutput metadataObjects: [AVMetadataObject], 
                       from connection: AVCaptureConnection) {
        guard !isProcessing,
              let metadataObject = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
              let code = metadataObject.stringValue else { return }
        
        DispatchQueue.main.async {
            self.isProcessing = true
        }
        
        APIService.shared.searchCompany(name: code) { [weak self] result in
            DispatchQueue.main.async {
                self?.isProcessing = false
                switch result {
                case .success(let scanResult):
                    self?.scanResult = scanResult
                case .failure(let error):
                    self?.errorMessage = String(format: "barcode_processing_failed".localized, error.localizedDescription)
                    print("Barcode processing error: \(error)")
                }
            }
        }
    }
}
