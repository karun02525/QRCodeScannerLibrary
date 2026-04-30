package org.qrcode.scanner.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.qrcode.scanner.presentation.ui.components.BottomControlsBar
import org.qrcode.scanner.presentation.ui.components.ScannerOverlayView
import org.qrcode.scanner.presentation.ui.components.TopScannerBar
import org.qrcode.scanner.qrcode.CameraContent
import org.qrcode.scanner.qrcode.rememberCameraPreviewState

@Composable
fun BuiltinOverlayScreen(
    onScanResult: (String) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // State variables for UI controls
    var isScanning by remember { mutableStateOf(true) }
    var isFlashOn by remember { mutableStateOf(false) }

    val scannerState = rememberCameraPreviewState()

    scannerState.onScanResult = { result, error ->
        if (result != null) {
            println("Found QR: $result")
            onScanResult(result)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        //Camera View
        scannerState.CameraContent()

        AnimatedVisibility(
            visible = isScanning,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            ScannerOverlayView(modifier = Modifier.fillMaxSize())
        }

        // ── 3. Top navigation bar (overlaid on camera) ─────────────────────
        TopScannerBar(
            onBack = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        )

        // ── 4. Bottom controls bar (Torch · Flip · Gallery) ────────────────
        BottomControlsBar(
            isFlashOn = isFlashOn,
            onToggleFlash = {
                isFlashOn = !isFlashOn
                scannerState.toggleFlash()
            },
            onFlipCamera = {
                scannerState.flipCamera()
            },
            onGallery = {
                scannerState.openGallery()
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        )
    }
}