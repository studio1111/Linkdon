package com.example.data.model

import androidx.compose.ui.text.font.FontFamily

enum class FontOption(
    val id: String,
    val titleFa: String,
    val fontFamily: FontFamily
) {
    DEFAULT("DEFAULT", "وزیرمتن / مدرن پیش‌فرض", FontFamily.Default),
    SANS_SERIF("SANS_SERIF", "سنس‌سریف نرم", FontFamily.SansSerif),
    SERIF("SERIF", "سریف کلاسیک", FontFamily.Serif),
    MONOSPACE("MONOSPACE", "مونو اسپیس کد", FontFamily.Monospace);

    companion object {
        fun fromId(id: String): FontOption {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: DEFAULT
        }
    }
}
