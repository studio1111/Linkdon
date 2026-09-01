package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.ui.graphics.vector.ImageVector

enum class ItemType(
    val id: String,
    val titleFa: String,
    val descriptionFa: String,
    val categoryGroupFa: String
) {
    BANK_CARD(
        id = "BANK_CARD",
        titleFa = "کارت و حساب بانکی",
        descriptionFa = "نام بانک، شماره ۱۶ رقمی کارت، نام مالک، CVV2، انقضا و شماره شبا",
        categoryGroupFa = "مالی و بانکی"
    ),
    CONTACT(
        id = "CONTACT",
        titleFa = "مخاطب و شماره تماس",
        descriptionFa = "نام مخاطب، شماره تلفن همراه/ثابت، ایمیل و یادداشت تماس",
        categoryGroupFa = "ارتباطی"
    ),
    SOCIAL_MEDIA(
        id = "SOCIAL_MEDIA",
        titleFa = "کانال و شبکه اجتماعی",
        descriptionFa = "تلگرام، اینستاگرام، یوتیوب، روبیکا، بله، ایتا، توییتر/X و...",
        categoryGroupFa = "رسانه و شبکه‌ها"
    ),
    URL(
        id = "URL",
        titleFa = "وب‌سایت / آدرس اینترنتی",
        descriptionFa = "آدرس URL به همراه عنوان، دسترسی سریع و توضیحات",
        categoryGroupFa = "وب و اینترنت"
    ),
    EMAIL_PASSWORD(
        id = "EMAIL_PASSWORD",
        titleFa = "ایمیل و رمز عبور",
        descriptionFa = "آدرس ایمیل یا نام کاربری به همراه گذرواژه امن",
        categoryGroupFa = "امنیتی و حساب‌ها"
    ),
    SECURITY_CODE(
        id = "SECURITY_CODE",
        titleFa = "کد امنیتی و احراز هویت",
        descriptionFa = "کدهای ۲ مرحله‌ای (2FA)، پین، کلید بازیابی و پسورد امن",
        categoryGroupFa = "امنیتی و حساب‌ها"
    ),
    AI_PROMPT(
        id = "AI_PROMPT",
        titleFa = "پرامپت هوش مصنوعی",
        descriptionFa = "پرامپت‌ها، دستورات و سیستم پرامپت‌ها با توضیحات",
        categoryGroupFa = "ابزارها و هوش مصنوعی"
    ),
    NOTE(
        id = "NOTE",
        titleFa = "یادداشت و متن مهم",
        descriptionFa = "یادداشت‌های متنی، ایده‌ها و نکات با قالب‌بندی",
        categoryGroupFa = "متنی"
    ),
    CODE_SNIPPET(
        id = "CODE_SNIPPET",
        titleFa = "کد و اسنیپت برنامه‌نویسی",
        descriptionFa = "قطعه کد به همراه زبان برنامه‌نویسی و عنوان",
        categoryGroupFa = "توسعه و کدنویسی"
    ),
    OTHER_TEXT(
        id = "OTHER_TEXT",
        titleFa = "سایر داده‌ها و متن دلخواه",
        descriptionFa = "هرگونه متن، لایسنس، کلید API یا داده دلخواه دیگر",
        categoryGroupFa = "متفرقه"
    );

    val icon: ImageVector
        get() = when (this) {
            BANK_CARD -> Icons.Default.CreditCard
            CONTACT -> Icons.Default.Person
            SOCIAL_MEDIA -> Icons.Default.Public
            URL -> Icons.Default.Language
            EMAIL_PASSWORD -> Icons.Default.AlternateEmail
            SECURITY_CODE -> Icons.Default.Password
            AI_PROMPT -> Icons.Default.AutoAwesome
            NOTE -> Icons.AutoMirrored.Filled.Notes
            CODE_SNIPPET -> Icons.Default.Code
            OTHER_TEXT -> Icons.Default.TextFields
        }

    companion object {
        fun fromId(id: String): ItemType {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: OTHER_TEXT
        }
    }
}
