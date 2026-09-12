package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun GlassFloatingAddButton(
    modifier: Modifier = Modifier,
    isDark: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF60A5FA), // light electric blue
                Color(0xFF2563EB), // rich vibrant blue
                Color(0xFF1D4ED8), // deep sapphire blue
                Color(0xFF0F172A).copy(alpha = 0.8f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFFAF7F2),
                Color(0xFFF3ECE0)
            )
        )
    }

    val borderBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFF93C5FD).copy(alpha = 0.8f),
                Color.White.copy(alpha = 0.25f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFD8CEBF),
                Color(0xFFB8A995)
            )
        )
    }

    GlassmorphicBox(
        modifier = modifier
            .size(64.dp)
            .testTag("floating_add_button"),
        shape = CircleShape,
        backgroundBrush = backgroundBrush,
        borderBrush = borderBrush,
        borderWidth = if (isDark) 2.dp else 1.5.dp,
        elevation = if (isDark) 14.dp else 6.dp,
        shadowColor = if (isDark) Color(0xFF1D4ED8).copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.15f),
        glowColor = if (isDark) Color(0xFF38BDF8) else Color(0xFFE2D9CC),
        onClick = onClick,
        contentAlignment = Alignment.Center
    ) {
        // Inner icon
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "افزودن",
                tint = if (isDark) Color.White else Color(0xFF1E293B),
                modifier = Modifier.size(34.dp)
            )
        }
    }
}
