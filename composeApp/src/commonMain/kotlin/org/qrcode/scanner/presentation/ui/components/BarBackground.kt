package org.qrcode.scanner.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// ── Background color of the controls strip ────────────────────────────────────
private val BarBackground = Color(0xFF1C1C1E)

@Composable
fun BottomControlsBar(
    isFlashOn: Boolean,
    onToggleFlash: () -> Unit,
    onFlipCamera: () -> Unit,
    onGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {

        HorizontalDivider(
            thickness = 0.5.dp,
            color = Color.White.copy(alpha = 0.12f),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 18.dp, horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FlashToggleButton(
                isFlashOn = isFlashOn,
                onToggle = onToggleFlash,
                showLabel = true,
                labelText = "Torch",
            )

            ControlItem(
                icon = Icons.Rounded.FlipCameraAndroid,
                label = "Flip",
                onClick = onFlipCamera,
            )

            ControlItem(
                icon = Icons.Rounded.Collections,
                label = "Gallery",
                onClick = onGallery,
            )
        }
    }
}

// ── Reusable control button item ──────────────────────────────────────────────
@Composable
private fun ControlItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.80f),
        )
    }
}