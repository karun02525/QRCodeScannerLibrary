package org.qrcode.scanner.presentation.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * DrawScope extension — draws four L-shaped corner brackets.
 * Call this directly inside any Canvas { } block.
 *
 *  ┌──         ──┐
 *  |               |
 *
 *  └──         ──┘
 */
fun DrawScope.drawCornerBrackets(
    left          : Float,
    top           : Float,
    right         : Float,
    bottom        : Float,
    bracketLength : Float,
    strokeWidth   : Float,
    color         : Color,
) {
    fun seg(start: Offset, end: Offset) = drawLine(
        color       = color,
        start       = start,
        end         = end,
        strokeWidth = strokeWidth,
        cap         = StrokeCap.Round,
    )

    // ┌ Top-Left
    seg(Offset(left, top + bracketLength), Offset(left, top))
    seg(Offset(left, top),                Offset(left + bracketLength, top))

    // ┐ Top-Right
    seg(Offset(right - bracketLength, top), Offset(right, top))
    seg(Offset(right, top),                 Offset(right, top + bracketLength))

    // └ Bottom-Left
    seg(Offset(left, bottom - bracketLength), Offset(left, bottom))
    seg(Offset(left, bottom),                 Offset(left + bracketLength, bottom))

    // ┘ Bottom-Right
    seg(Offset(right - bracketLength, bottom), Offset(right, bottom))
    seg(Offset(right, bottom),                 Offset(right, bottom - bracketLength))
}