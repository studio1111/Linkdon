package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.FontOption
import com.example.data.model.ThemeOption

private val LinkdoonDarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF0284C7),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = Color(0xFF818CF8),
    onSecondary = Color(0xFF1E1B4B),
    background = Color(0xFF0B0F19),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131A2A),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val LinkdoonLightColorScheme = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFF4F46E5),
    onSecondary = Color.White,
    background = Color(0xFFFBF9F5),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3EFE7),
    onSurfaceVariant = Color(0xFF374151),
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun LinkdoonTheme(
    themeOption: ThemeOption = ThemeOption.DARK,
    fontOption: FontOption = FontOption.DEFAULT,
    textScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val baseScheme = if (themeOption.isDark) LinkdoonDarkColorScheme else LinkdoonLightColorScheme
    val colorScheme = baseScheme.copy(
        primary = themeOption.accentColor,
        primaryContainer = themeOption.startGradient,
        background = themeOption.startGradient,
        surface = themeOption.endGradient
    )

    val typography = createLinkdoonTypography(fontOption, textScale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
