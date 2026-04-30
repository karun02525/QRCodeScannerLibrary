package org.qrcode.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.qrcode.scanner.presentation.ui.BuiltinOverlayScreen
import org.qrcode.scanner.presentation.ui.HomeScreen
import org.qrcode.scanner.qrcode.CameraContent
import org.qrcode.scanner.qrcode.rememberCameraPreviewState
import org.qrcode.scanner.ui.theme.QRScannerTheme
import androidx.compose.material3.Text


@Composable
fun App() {
    QRScannerTheme {
        //WithoutOverlayScreen()
        ActionBuiltinOverlayScreen()
    }
}

@Composable
fun TextScreen() =
Text(
    text = "Hello, World!",
    modifier = Modifier.padding(16.dp)
)

@Composable
fun WithoutOverlayScreen() {
    val scannerState = rememberCameraPreviewState()
    scannerState.onScanResult = { result, error ->
        if (result != null) {
            println("Found QR: $result")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        //Camera View
        scannerState.CameraContent()

        Button(
            onClick = { scannerState.toggleFlash() },
            modifier = Modifier
                .padding(bottom = 80.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text("Torch")
        }
    }
}

@Composable
fun ActionBuiltinOverlayScreen() {
    val scannerState = rememberCameraPreviewState()
    var scanResult by remember { mutableStateOf<String?>(null) }
    var isClick by remember { mutableStateOf(false) }
    if (isClick) {
        BuiltinOverlayScreen(
            onScanResult = { result ->
                scanResult = result
                isClick = false
            },
            onBack = {
                isClick = false
            }
        )
    } else {
        HomeScreen(
            onLaunchScanner = {
                isClick = true
            },
            scannedValue = scanResult
        )
    }
}
