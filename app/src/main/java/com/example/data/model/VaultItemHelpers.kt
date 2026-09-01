package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BankCardInfo(
    val bankName: String = "",
    val cardNumber: String = "",
    val ownerName: String = "",
    val cvv2: String = "",
    val expireMonth: String = "",
    val expireYear: String = "",
    val shebaNumber: String = ""
) {
    fun formatCardNumber(): String {
        val clean = cardNumber.replace("\\s+".toRegex(), "")
        return clean.chunked(4).joinToString(" - ")
    }

    fun formatSheba(): String {
        val clean = shebaNumber.replace("\\s+".toRegex(), "").uppercase()
        if (clean.isBlank()) return ""
        return if (clean.startsWith("IR")) clean else "IR$clean"
    }

    fun getExpireFormatted(): String {
        return if (expireMonth.isNotBlank() && expireYear.isNotBlank()) {
            "$expireMonth / $expireYear"
        } else if (expireMonth.isNotBlank()) {
            expireMonth
        } else ""
    }
}

@JsonClass(generateAdapter = true)
data class ContactInfo(
    val fullName: String = "",
    val phoneNumber: String = "",
    val secondaryPhone: String = "",
    val email: String = "",
    val note: String = ""
)

@JsonClass(generateAdapter = true)
data class SocialMediaInfo(
    val platform: String = "تلگرام", // تلگرام، اینستاگرام، یوتیوب، روبیکا، بله، ایتا، توییتر/X، لینکدین، گیت‌هاب، سایر
    val usernameOrChannel: String = "",
    val directLink: String = "",
    val description: String = ""
)
