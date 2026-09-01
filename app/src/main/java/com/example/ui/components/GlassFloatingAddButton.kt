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
    onClick: () -> Unit
) {
    GlassmorphicBox(
        modifier = modifier
            .size(64.dp)
            .testTag("floating_add_button"),
        shape = CircleShape,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF60A5FA), // light electric blue
                Color(0xFF2563EB), // rich vibrant blue
                Color(0xFF1D4ED8), // deep sapphire blue
                Color(0xFF0F172A).copy(alpha = 0.8f)
            )
        ),
        borderBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFF93C5FD).copy(alpha = 0.8f),
                Color.White.copy(alpha = 0.25f)
            )
        ),
        borderWidth = 2.dp,
        elevation = 14.dp,
        shadowColor = Color(0xFF1D4ED8).copy(alpha = 0.6f),
        glowColor = Color(0xFF38BDF8),
        onClick = onClick,
        contentAlignment = Alignment.Center
    ) {
        // Inner 3D specular glow highlight ring
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "افزودن",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}
