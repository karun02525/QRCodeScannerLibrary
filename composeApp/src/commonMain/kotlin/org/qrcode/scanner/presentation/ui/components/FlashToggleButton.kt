package org.qrcode.scanner.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FlashToggleButton(
    isFlashOn   : Boolean,
    onToggle    : () -> Unit,
    modifier    : Modifier = Modifier,
    buttonSize  : Dp      = 52.dp,
    showLabel   : Boolean  = true,
    labelText   : String   = "Torch",
) {
    val bgColor by animateColorAsState(
        targetValue   = if (isFlashOn) Color(0xFFFFD60A) else Color.White.copy(alpha = 0.18f),
        animationSpec = spring(),
        label         = "flash_bg",
    )
    val iconColor = if (isFlashOn) Color.Black else Color.White

    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(bgColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onToggle,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = if (isFlashOn) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                contentDescription = if (isFlashOn) "Flash On" else "Flash Off",
                tint               = iconColor,
                modifier           = Modifier.size(26.dp),
            )
        }

        if (showLabel) {
            Text(
                text  = labelText,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.80f),
            )
        }
    }
}