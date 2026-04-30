package org.qrcode.scanner.bridge

import androidx.compose.runtime.Composable
import org.qrcode.scanner.qrcode.CameraPreviewState

interface NativeBridgeUI {
    @Composable
    fun QRCodeScannerUI(state: CameraPreviewState)
}

