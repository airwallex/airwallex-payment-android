package com.airwallex.android.view

import androidx.annotation.DrawableRes
import com.airwallex.android.R

/**
 * All card brands supported by Airwallex. Only support Visa, Mastercard & American Express
 */
enum class CardBrand(
    val type: String,
    @DrawableRes val icon: Int,
    private val prefixes: Set<String> = emptySet(),
    private val fullName: String = type,
    val spacingPattern: List<Int> = listOf(4, 4, 4, 4),
    val lengthRange: IntRange = 16..16
) {
    Visa(
        "visa",
        R.drawable.airwallex_ic_visa,
        prefixes = setOf("4")
    ),
    MasterCard(
        "mastercard",
        R.drawable.airwallex_ic_mastercard,
        prefixes = setOf(
            "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224",
            "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720",
            "50", "51", "52", "53", "54", "55", "67"
        )
    ),
    Amex(
        "amex",
        R.drawable.airwallex_ic_amex,
        prefixes = setOf("34", "37"),
        fullName = "american express",
        spacingPattern = listOf(4, 6, 5),
        lengthRange = 15..15
    ),
    UnionPay(
        "unionpay",
        R.drawable.airwallex_ic_unionpay,
        fullName = "union pay",
        prefixes = setOf("62"),
        lengthRange = 16..19
    ),
    Unknown(
        "unknown",
        R.drawable.airwallex_ic_card_default
    );

    companion object {

        /**
         * Get the [CardBrand] that matches the number's prefix
         *
         * @param number the credit card number
         */
        fun fromCardNumber(number: String?): CardBrand {
            return values().firstOrNull { brand ->
                brand.prefixes.takeIf { it.isNotEmpty() }?.any {
                    number?.startsWith(
                        it
                    ) == true
                } == true
            } ?: Unknown
        }

        fun fromType(type: String) = values().associateBy(CardBrand::type)[type]
        fun fromName(name: String) = values().associateBy(CardBrand::fullName)[name]
    }
}
