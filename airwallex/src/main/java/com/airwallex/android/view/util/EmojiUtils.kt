package com.airwallex.android.view.util

fun getFlagEmoji(countryCode: String): String {
    return countryCode.uppercase().map {
        val charCode = (it.code - 'A'.code + 0x1F1E6)
        String(Character.toChars(charCode))
    }.joinToString("")
}