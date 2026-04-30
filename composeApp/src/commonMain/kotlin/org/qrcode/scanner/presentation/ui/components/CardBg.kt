package org.qrcode.scanner.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Design tokens ─────────────────────────────────────────────────────────────
private val CardBg    = Color(0xFFF2F2F7)  // iOS system grouped background
private val IosBlue   = Color(0xFF007AFF)
private val DividerC  = Color(0xFFE5E5EA)

@Composable
fun QRResultCard(
    scannedValue : String,
    onCopy       : () -> Unit,
    modifier     : Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── "Scanned via Camera" header ────────────────────────────────────
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier              = Modifier.padding(bottom = 10.dp),
        ) {
            Icon(
                imageVector        = Icons.Rounded.CameraAlt,
                contentDescription = null,
                tint               = IosBlue,
                modifier           = Modifier.size(15.dp),
            )
            Text(
                text       = "Scanned via Camera",
                style      = MaterialTheme.typography.labelMedium,
                color      = IosBlue,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // ── Scanned value ──────────────────────────────────────────────────
        Text(
            text       = scannedValue,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color      = Color.Black,
            modifier   = Modifier.padding(bottom = 12.dp),
        )

        // ── Divider ────────────────────────────────────────────────────────
        HorizontalDivider(
            color     = DividerC,
            thickness = 0.5.dp,
            modifier  = Modifier.padding(bottom = 10.dp),
        )

        // ── Copy to Clipboard ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onCopy,
                ),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Icon(
                imageVector        = Icons.Rounded.ContentCopy,
                contentDescription = "Copy to Clipboard",
                tint               = IosBlue,
                modifier           = Modifier.size(15.dp),
            )
            Text(
                text  = "Copy to Clipboard",
                style = MaterialTheme.typography.labelMedium,
                color = IosBlue,
            )
        }
    }
}