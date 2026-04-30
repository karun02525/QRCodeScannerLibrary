package org.qrcode.scanner.qrcode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.koinInject
import org.qrcode.scanner.bridge.NativeBridgeUI

class CameraPreviewState {
    var openGallery: () -> Unit = {}
    var toggleFlash: () -> Unit = {}
    var flipCamera: () -> Unit = {}

    var onScanResult: (String?, String?) -> Unit = { _, _ -> }
}

@Composable
fun rememberCameraPreviewState(): CameraPreviewState {
    return remember { CameraPreviewState() }
}

@Composable
fun CameraPreviewState.CameraContent() {
    val uiProvider:NativeBridgeUI = koinInject()
    uiProvider.QRCodeScannerUI(state = this)
}