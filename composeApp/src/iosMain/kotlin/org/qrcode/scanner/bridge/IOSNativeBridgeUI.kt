package org.qrcode.scanner.bridge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import org.qrcode.scanner.qrcode.CameraPreviewState
import platform.UIKit.UIViewController

class IOSNativeBridgeUI : NativeBridgeUI {

    @Composable
    override fun QRCodeScannerUI(state: CameraPreviewState) {
        val bridge = iosBridge ?: return
        UIKitViewController(
            factory = { bridge.qrCodeScannerUi(state) as UIViewController },
            modifier = Modifier.fillMaxSize()
        )
    }
}

