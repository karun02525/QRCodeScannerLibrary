package org.qrcode.scanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = darkColorScheme(
    primary         = Color(0xFF00E676),
    secondary       = Color(0xFF69F0AE),
    background      = Color(0xFF0D0D0D),
    surface         = Color(0xFF1A1A1A),
    onPrimary       = Color.Black,
    onBackground    = Color.White,
    onSurface       = Color.White,
    onSurfaceVariant = Color(0xFFB0B0B0),
)

@Composable
fun QRScannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content     = content,
    )
}