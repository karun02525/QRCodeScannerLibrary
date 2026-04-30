package org.qrcode.scanner.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.qrcode.scanner.presentation.ui.components.QRResultCard

private val IosBlue   = Color(0xFF007AFF)
private val ScreenBg  = Color(0xFFFFFFFF)
private val SubtitleC = Color(0xFF8A8A8E)

@Composable
fun HomeScreen(
    scannedValue   : String?= null,
    onLaunchScanner: () -> Unit ={},
    onCopyValue    : () -> Unit = {},
    modifier       : Modifier = Modifier,
) {
    Scaffold(
        containerColor = ScreenBg,
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(ScreenBg),
        ) {
            Text(
                text       = "Scanner",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = Color.Black,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 20.dp, bottom = 0.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement   = Arrangement.Center,
                horizontalAlignment   = Alignment.CenterHorizontally,
            ) {
                // QR Icon
                Icon(
                    imageVector        = Icons.Rounded.QrCodeScanner,
                    contentDescription = "QR Scanner",
                    tint               = IosBlue,
                    modifier           = Modifier.size(76.dp),
                )

                Spacer(Modifier.height(18.dp))

                // App title
                Text(
                    text       = "QR Scanner Kit",
                    fontSize   = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.Black,
                    textAlign  = TextAlign.Center,
                )

                Spacer(Modifier.height(10.dp))

                // Subtitle
                Text(
                    text      = "Scan any QR code or Barcode to see the\nmagic happen.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = SubtitleC,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                )

                Spacer(Modifier.height(32.dp))

                // ── Result card (animated slide-in) ───────────────────────
                AnimatedVisibility(
                    visible = scannedValue != null,
                    enter   = slideInVertically(tween(350)) { it / 2 } + fadeIn(tween(350)),
                    exit    = slideOutVertically(tween(300)) { it / 2 } + fadeOut(tween(300)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                ) {
                    scannedValue?.let { value ->
                        QRResultCard(
                            scannedValue = value,
                            onCopy       = onCopyValue,
                        )
                    }
                }
            }

            // ── "Launch Scanner" button ────────────────────────────────────
            Button(
                onClick  = onLaunchScanner,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding(),
                colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
                shape  = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    imageVector        = Icons.Rounded.QrCodeScanner,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text       = "Launch Scanner",
                    color      = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 17.sp,
                )
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}