package io.github.silascui.ktIztro.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Android Material 3 形状体系 (按 DESIGN.md 规范)
 * sm: 2.dp (0.125rem)
 * DEFAULT: 4.dp (0.25rem)
 * md: 6.dp (0.375rem)
 * lg: 8.dp (0.5rem)
 * xl: 12.dp (0.75rem)
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(12.dp)
)
