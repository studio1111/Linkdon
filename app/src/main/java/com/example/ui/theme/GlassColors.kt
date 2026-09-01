package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class GlassColorItem(
    val hex: String,
    val nameFa: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val highlightColor: Color
)

object GlassColors {
    val presets = listOf(
        GlassColorItem(
            hex = "#3B82F6",
            nameFa = "آبی لاجوردی",
            primaryColor = Color(0xFF1E40AF),
            secondaryColor = Color(0xFF3B82F6),
            highlightColor = Color(0xFF93C5FD)
        ),
        GlassColorItem(
            hex = "#10B981",
            nameFa = "سبز زمردی",
            primaryColor = Color(0xFF065F46),
            secondaryColor = Color(0xFF10B981),
            highlightColor = Color(0xFF6EE7B7)
        ),
        GlassColorItem(
            hex = "#EF4444",
            nameFa = "قرمز یاقوتی",
            primaryColor = Color(0xFF991B1B),
            secondaryColor = Color(0xFFEF4444),
            highlightColor = Color(0xFFFCA5A5)
        ),
        GlassColorItem(
            hex = "#8B5CF6",
            nameFa = "بنفش آمتیست",
            primaryColor = Color(0xFF5B21B6),
            secondaryColor = Color(0xFF8B5CF6),
            highlightColor = Color(0xFFC4B5FD)
        ),
        GlassColorItem(
            hex = "#F97316",
            nameFa = "نارنجی خورشیدی",
            primaryColor = Color(0xFF9A3412),
            secondaryColor = Color(0xFFF97316),
            highlightColor = Color(0xFFFDBA74)
        ),
        GlassColorItem(
            hex = "#06B6D4",
            nameFa = "فیروزه‌ای نئون",
            primaryColor = Color(0xFF155E75),
            secondaryColor = Color(0xFF06B6D4),
            highlightColor = Color(0xFF67E8F9)
        ),
        GlassColorItem(
            hex = "#EC4899",
            nameFa = "صورتی ماژنتا",
            primaryColor = Color(0xFF831843),
            secondaryColor = Color(0xFFEC4899),
            highlightColor = Color(0xFFF472B6)
        ),
        GlassColorItem(
            hex = "#EAB308",
            nameFa = "طلایی کهربایی",
            primaryColor = Color(0xFF854D0E),
            secondaryColor = Color(0xFFEAB308),
            highlightColor = Color(0xFFFDE047)
        ),
        GlassColorItem(
            hex = "#14B8A6",
            nameFa = "سبز دریایی عمیق",
            primaryColor = Color(0xFF115E59),
            secondaryColor = Color(0xFF14B8A6),
            highlightColor = Color(0xFF5EEAD4)
        ),
        GlassColorItem(
            hex = "#6366F1",
            nameFa = "نیلی کیهانی",
            primaryColor = Color(0xFF312E81),
            secondaryColor = Color(0xFF6366F1),
            highlightColor = Color(0xFFA5B4FC)
        ),
        GlassColorItem(
            hex = "#D946EF",
            nameFa = "ارغوانی درخشان",
            primaryColor = Color(0xFF701A75),
            secondaryColor = Color(0xFFD946EF),
            highlightColor = Color(0xFFF0ABFC)
        ),
        GlassColorItem(
            hex = "#22C55E",
            nameFa = "سبز نئونی روشن",
            primaryColor = Color(0xFF14532D),
            secondaryColor = Color(0xFF22C55E),
            highlightColor = Color(0xFF86EFAC)
        ),
        GlassColorItem(
            hex = "#F43F5E",
            nameFa = "مرجانی شعله‌ور",
            primaryColor = Color(0xFF881337),
            secondaryColor = Color(0xFFF43F5E),
            highlightColor = Color(0xFFFDA4AF)
        ),
        GlassColorItem(
            hex = "#64748B",
            nameFa = "خاکستری تیتانیوم",
            primaryColor = Color(0xFF1E293B),
            secondaryColor = Color(0xFF64748B),
            highlightColor = Color(0xFF94A3B8)
        ),
        GlassColorItem(
            hex = "#0284C7",
            nameFa = "آبی کبالت اقیانوس",
            primaryColor = Color(0xFF075985),
            secondaryColor = Color(0xFF0284C7),
            highlightColor = Color(0xFF38BDF8)
        ),
        GlassColorItem(
            hex = "#78350F",
            nameFa = "برنز کاراملی لوکس",
            primaryColor = Color(0xFF451A03),
            secondaryColor = Color(0xFF78350F),
            highlightColor = Color(0xFFD97706)
        )
    )

    fun getColorItem(hex: String): GlassColorItem {
        return presets.firstOrNull { it.hex.equals(hex, ignoreCase = true) } ?: presets[0]
    }

    fun getGlassCardBrush(hex: String, isDark: Boolean): Brush {
        val item = getColorItem(hex)
        return if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    item.primaryColor.copy(alpha = 0.55f),
                    item.secondaryColor.copy(alpha = 0.35f),
                    Color(0xFF0F172A).copy(alpha = 0.45f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    item.secondaryColor.copy(alpha = 0.25f),
                    item.highlightColor.copy(alpha = 0.18f),
                    Color.White.copy(alpha = 0.65f)
                )
            )
        }
    }

    fun getCardBorderBrush(hex: String, isDark: Boolean): Brush {
        val item = getColorItem(hex)
        return if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.45f),
                    item.highlightColor.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.85f),
                    item.secondaryColor.copy(alpha = 0.40f),
                    Color.White.copy(alpha = 0.30f)
                )
            )
        }
    }

    fun getGlassButtonBrush(isDark: Boolean = true): Brush {
        return if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.18f),
                    Color.White.copy(alpha = 0.06f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.70f),
                    Color.White.copy(alpha = 0.40f)
                )
            )
        }
    }

    fun getGlassBorderBrush(isDark: Boolean = true): Brush {
        return if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.45f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.80f),
                    Color.White.copy(alpha = 0.25f)
                )
            )
        }
    }
}
