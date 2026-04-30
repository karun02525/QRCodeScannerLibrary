
import Foundation
import qrscanner
import SwiftUI
import QRCodeScanner


class IOSBridgeImpl: IOSBridge {

    // MARK: - Toast
    func showToast(message: String) {
        DispatchQueue.main.async {
            let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
            alert.view.alpha = 0.6
            alert.view.layer.cornerRadius = 15

            guard
                let keyWindow = UIApplication.shared.connectedScenes
                    .compactMap({ $0 as? UIWindowScene })
                    .flatMap({ $0.windows })
                    .first(where: { $0.isKeyWindow }),
                let rootVC = keyWindow.rootViewController
            else { return }

            var topVC = rootVC
            while let presentedVC = topVC.presentedViewController {
                topVC = presentedVC
            }

            topVC.present(alert, animated: true)

            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                alert.dismiss(animated: true)
            }
        }
    }

    // MARK: - Share
    func share(message: String) {
        // FIX 3: Same main-thread dispatch as showToast.
        DispatchQueue.main.async {
            let vc = UIActivityViewController(activityItems: [message], applicationActivities: nil)
            guard
                let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                let root = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController
            else { return }

            var top = root
            while let presented = top.presentedViewController { top = presented }
            top.present(vc, animated: true)
        }
    }

    // MARK: - QR Scanner
    func qrCodeScannerUi(state: CameraPreviewState) -> Any {
        let scannerController = ScannerController()
        
        scannerController.onScan = { code in
            DispatchQueue.main.async {
                state.onScanResult(code, nil)
            }
        }

        state.toggleFlash = { [weak scannerController] in
            guard let scannerController else { return }
            DispatchQueue.main.async {
                scannerController.toggleTorch()
            }
        }

        state.flipCamera = { [weak scannerController] in
            guard let scannerController else { return }
            DispatchQueue.main.async {
                scannerController.flipCamera()
            }
        }

        state.openGallery = { [weak scannerController] in
            guard let scannerController else { return }
            DispatchQueue.main.async {
                scannerController.openGallery()
            }
        }

        let view = ScannerContainerView(
            controller: scannerController,
            isScannerOverlay: false
        )

        return UIHostingController(rootView: view)
    }
}
