import AVFoundation
import UIKit

class ScannerViewModel: NSObject, ObservableObject, AVCaptureMetadataOutputObjectsDelegate {
    @Published var session = AVCaptureSession()
    @Published var isTorchOn = false
    @Published var scanResult: ScanResult?
    @Published var isProcessing = false
    @Published var isCameraReady = false
    @Published var errorMessage: String?
    
    private let captureDevice = AVCaptureDevice.default(for: .video)
    
    override init() {
        super.init()
        setupCamera()
    }
    
    func setupCamera() {
        guard let device = captureDevice else { 
            DispatchQueue.main.async {
                self.errorMessage = "无法访问相机"
            }
            return 
        }
        
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            guard let self = self else { return }
            
            if self.session.isRunning {
                self.session.stopRunning()
            }
            
            self.session.beginConfiguration()
            
            self.session.inputs.forEach { self.session.removeInput($0) }
            self.session.outputs.forEach { self.session.removeOutput($0) }
            
            do {
                let input = try AVCaptureDeviceInput(device: device)
                if self.session.canAddInput(input) {
                    self.session.addInput(input)
                }
                
                let output = AVCaptureMetadataOutput()
                if self.session.canAddOutput(output) {
                    self.session.addOutput(output)
                    output.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
                    output.metadataObjectTypes = [.qr, .ean13, .ean8, .code128]
                }
                
                self.session.commitConfiguration()
                
                DispatchQueue.main.async {
                    self.isCameraReady = true
                    if !self.session.isRunning {
                        self.session.startRunning()
                    }
                }
            } catch {
                print("Camera setup error: \(error)")
                DispatchQueue.main.async {
                    self.isCameraReady = false
                    self.errorMessage = "相机初始化失败: \(error.localizedDescription)"
                }
            }
        }
    }
    
    func toggleTorch() {
        guard let device = captureDevice, device.hasTorch else { 
            DispatchQueue.main.async {
                self.errorMessage = "设备不支持闪光灯"
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
                self.errorMessage = "闪光灯控制失败"
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
                    self?.errorMessage = "图像识别失败: \(error.localizedDescription)"
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
                    self?.errorMessage = "条码处理失败: \(error.localizedDescription)"
                    print("Barcode processing error: \(error)")
                }
            }
        }
    }
}
