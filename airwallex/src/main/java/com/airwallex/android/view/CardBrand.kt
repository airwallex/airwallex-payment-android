package com.airwallex.android.view

import androidx.annotation.DrawableRes
import com.airwallex.android.R

enum class CardBrand(
    val type: String,
    @DrawableRes val icon: Int,
    private val prefixes: Set<String> = emptySet()
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
    Unknown(
        "unknown",
        R.drawable.airwallex_ic_card_default
    );

    companion object {
        internal fun fromCardNumber(number: String?): CardBrand {
            return values().firstOrNull { brand ->
                brand.prefixes.takeIf { it.isNotEmpty() }?.any {
                    number?.startsWith(
                        it
                    ) == true
                } == true
            } ?: Unknown
        }
    }
}
