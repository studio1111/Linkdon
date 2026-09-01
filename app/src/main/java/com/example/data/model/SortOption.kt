package com.example.data.model

enum class SortOption(
    val id: String,
    val titleFa: String,
    val description: String
) {
    DATE_NEWEST(
        id = "date_newest",
        titleFa = "تاریخ ساخت (جدیدترین)",
        description = "نمایش جدیدترین آیتم‌ها در بالا"
    ),
    DATE_OLDEST(
        id = "date_oldest",
        titleFa = "تاریخ ساخت (قدیمی‌ترین)",
        description = "نمایش اولین آیتم‌های ثبت‌شده"
    ),
    ALPHABETICAL_ASC(
        id = "alpha_asc",
        titleFa = "حروف الفبا (الف تا ی / A-Z)",
        description = "مرتب‌سازی صعودی بر اساس نام"
    ),
    ALPHABETICAL_DESC(
        id = "alpha_desc",
        titleFa = "حروف الفبا (ی تا الف / Z-A)",
        description = "مرتب‌سازی نزولی بر اساس نام"
    ),
    RATING_HIGHEST(
        id = "rating_highest",
        titleFa = "امتیاز (بیشترین امتیاز ★)",
        description = "نمایش آیتم‌های ۵ ستاره و برگزیده"
    ),
    RATING_LOWEST(
        id = "rating_lowest",
        titleFa = "امتیاز (کمترین امتیاز)",
        description = "نمایش آیتم‌ها از کمترین امتیاز"
    );

    companion object {
        fun fromId(id: String): SortOption {
            return entries.firstOrNull { it.id == id } ?: DATE_NEWEST
        }
    }
}
