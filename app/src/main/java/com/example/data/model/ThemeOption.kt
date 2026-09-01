package com.example.data.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class ThemeOption(
    val id: String,
    val titleFa: String,
    val isDark: Boolean,
    val previewColor: Color,
    val startGradient: Color,
    val endGradient: Color,
    val accentColor: Color
) {
    DARK(
        id = "DARK",
        titleFa = "تم تاریک کلاسیک",
        isDark = true,
        previewColor = Color(0xFF1E293B),
        startGradient = Color(0xFF0F172A),
        endGradient = Color(0xFF1E293B),
        accentColor = Color(0xFF38BDF8)
    ),
    LIGHT(
        id = "LIGHT",
        titleFa = "تم روشن شیشه‌ای",
        isDark = false,
        previewColor = Color(0xFFE2E8F0),
        startGradient = Color(0xFFE2E8F0),
        endGradient = Color(0xFFF8FAFC),
        accentColor = Color(0xFF2563EB)
    ),
    DARK_GREEN(
        id = "DARK_GREEN",
        titleFa = "سبز تیره زمردی",
        isDark = true,
        previewColor = Color(0xFF064E3B),
        startGradient = Color(0xFF022C22),
        endGradient = Color(0xFF065F46),
        accentColor = Color(0xFF10B981)
    ),
    DARK_BLUE(
        id = "DARK_BLUE",
        titleFa = "آبی تیره اقیانوسی",
        isDark = true,
        previewColor = Color(0xFF1E3A8A),
        startGradient = Color(0xFF0F172A),
        endGradient = Color(0xFF1E40AF),
        accentColor = Color(0xFF60A5FA)
    ),
    DARK_PURPLE(
        id = "DARK_PURPLE",
        titleFa = "بنفش تیره کهکشانی",
        isDark = true,
        previewColor = Color(0xFF4C1D95),
        startGradient = Color(0xFF1E1B4B),
        endGradient = Color(0xFF581C87),
        accentColor = Color(0xFFA855F7)
    ),
    PINK(
        id = "PINK",
        titleFa = "صورتی نئونی لوکس",
        isDark = true,
        previewColor = Color(0xFF831843),
        startGradient = Color(0xFF4C0519),
        endGradient = Color(0xFF9D174D),
        accentColor = Color(0xFFF472B6)
    );

    val backgroundBrush: Brush
        get() = Brush.linearGradient(
            colors = listOf(startGradient, endGradient)
        )

    companion object {
        fun fromId(id: String): ThemeOption {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: DARK
        }
    }
}
