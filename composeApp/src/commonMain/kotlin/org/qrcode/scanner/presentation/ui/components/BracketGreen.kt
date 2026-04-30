package org.qrcode.scanner.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// ── Design Tokens ────────────────────────────────────────────────────────────
private val BracketGreen   = Color(0xFF00E676)   // vivid green brackets
private val OverlayBlack   = Color(0xCC000000)   // 80% black overlay
private const val WIN_FRAC = 0.78f               // window width / screen width
private const val WIN_TOP  = 0.19f               // window top / screen height

@Composable
fun ScannerOverlayView(modifier: Modifier = Modifier) {

    // ── Continuous animations ─────────────────────────────────────────────────
    val infinite = rememberInfiniteTransition(label = "scanner_anim")

    // Scan line sweeps top → bottom → top
    val scanProgress by infinite.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scan_line",
    )

    // Bracket pulse (opacity breathe)
    val bracketAlpha by infinite.animateFloat(
        initialValue  = 0.65f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bracket_alpha",
    )

    // Corner scale breathe
    val bracketScale by infinite.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.03f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bracket_scale",
    )

    // Scanning dots: •  ••  •••
    var dotCount by remember { mutableStateOf(1) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500L)
            dotCount = if (dotCount >= 3) 1 else dotCount + 1
        }
    }

    BoxWithConstraints(modifier = modifier) {
        // ── Dp-space window metrics (for Compose layout) ──────────────────────
        val winSizeDp  = maxWidth * WIN_FRAC
        val winTopDp   = maxHeight * WIN_TOP
        val winBottomDp = winTopDp + winSizeDp

        // ── Canvas overlay ────────────────────────────────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            val wSize   = size.width * WIN_FRAC
            val wLeft   = (size.width - wSize) / 2f
            val wTop    = size.height * WIN_TOP
            val wRight  = wLeft + wSize
            val wBottom = wTop + wSize

            // 1 ── Dark overlay (full screen) ─────────────────────────────────
            drawRect(color = OverlayBlack)

            // 2 ── Transparent punch-through window ───────────────────────────
            drawRect(
                color     = Color.Transparent,
                topLeft   = Offset(wLeft, wTop),
                size      = Size(wSize, wSize),
                blendMode = BlendMode.Clear,
            )

            // 3 ── Thin window border ──────────────────────────────────────────
            drawRect(
                color   = BracketGreen.copy(alpha = 0.28f),
                topLeft = Offset(wLeft, wTop),
                size    = Size(wSize, wSize),
                style   = Stroke(width = 1.dp.toPx()),
            )

            // 4 ── Animated corner brackets ────────────────────────────────────
            val centerX = wLeft + wSize / 2f
            val centerY = wTop  + wSize / 2f
            // Apply scale from center of the window
            val scaledLeft   = centerX - (wSize / 2f) * bracketScale
            val scaledTop    = centerY - (wSize / 2f) * bracketScale
            val scaledRight  = centerX + (wSize / 2f) * bracketScale
            val scaledBottom = centerY + (wSize / 2f) * bracketScale

            drawCornerBrackets(
                left          = scaledLeft,
                top           = scaledTop,
                right         = scaledRight,
                bottom        = scaledBottom,
                bracketLength = 42.dp.toPx(),
                strokeWidth   = 4.dp.toPx(),
                color         = BracketGreen.copy(alpha = bracketAlpha),
            )

            // 5 ── Scan line glow (wide, soft halo) ───────────────────────────
            val lineY = wTop + scanProgress * wSize
            val inset = 18.dp.toPx()

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        BracketGreen.copy(alpha = 0.13f),
                        BracketGreen.copy(alpha = 0.13f),
                        Color.Transparent,
                    ),
                    startX = wLeft + inset,
                    endX   = wRight - inset,
                ),
                topLeft = Offset(wLeft + inset, lineY - 9.dp.toPx()),
                size    = Size(wSize - inset * 2, 18.dp.toPx()),
            )

            // 6 ── Scan line (bright core) ─────────────────────────────────────
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.55f),
                        Color.White,
                        Color.White,
                        Color.White.copy(alpha = 0.55f),
                        Color.Transparent,
                    ),
                    startX = wLeft + inset,
                    endX   = wRight - inset,
                ),
                topLeft = Offset(wLeft + inset, lineY - 1.dp.toPx()),
                size    = Size(wSize - inset * 2, 2.dp.toPx()),
            )
        }

        // ── Hint text — rendered below the scan window ────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = winBottomDp + 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text      = "Align QR code or barcode within the frame",
                style     = MaterialTheme.typography.bodySmall,
                color     = Color.White,
                textAlign = TextAlign.Center,
            )

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector        = Icons.Rounded.QrCodeScanner,
                    contentDescription = null,
                    tint               = Color.White.copy(alpha = 0.75f),
                    modifier           = Modifier.size(15.dp),
                )
                Text(
                    text  = "Scanning ${"•".repeat(dotCount)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.75f),
                )
            }
        }
    }
}