package org.qrcode.scanner.bridge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.qrcode.scanner.qrcode.CameraPreview
import org.qrcode.scanner.qrcode.CameraPreviewState
import org.qrcode.scanner.qrcode.rememberCameraPreviewStateAndroid


class AndroidNativeUI : NativeBridgeUI {

    @Composable
    override fun QRCodeScannerUI(state: CameraPreviewState) {

        val nativeCameraState = rememberCameraPreviewStateAndroid(
            onScanResult = { result, error ->
                state.onScanResult(result, error)
            }
        )

        LaunchedEffect(nativeCameraState) {
            state.openGallery = {
                nativeCameraState.openGallery()
            }

            state.toggleFlash = {
                nativeCameraState.toggleTorch()
            }

            state.flipCamera = {
                nativeCameraState.flipCamera()
            }
        }

        // 3. Render the Native Preview
        CameraPreview(
            state = nativeCameraState,
            modifier = Modifier.fillMaxSize(),
        )
    }
}