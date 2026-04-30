package org.qrcode.scanner.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TopScannerBar(
    title    : String   = "Scan Code",
    onBack   : () -> Unit,
    onHelp   : () -> Unit = {},
    modifier : Modifier  = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // ── Back button ────────────────────────────────────────────────────
        RoundIconButton(onClick = onBack) {
            Icon(
                imageVector        = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                tint               = Color.White,
                modifier           = Modifier.size(20.dp),
            )
        }

        // ── Title ──────────────────────────────────────────────────────────
        Text(
            text       = title,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color      = Color.White,
        )

        // ── Help "?" button ────────────────────────────────────────────────
        RoundIconButton(onClick = onHelp) {
            Text(
                text  = "?",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ── Small circular button ─────────────────────────────────────────────────────
@Composable
private fun RoundIconButton(
    onClick : () -> Unit,
    content : @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.20f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            ),
        contentAlignment = Alignment.Center,
        content          = { content() },
    )
}