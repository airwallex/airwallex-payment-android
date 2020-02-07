package com.airwallex.paymentacceptance.view

import androidx.annotation.DrawableRes
import com.airwallex.paymentacceptance.R

enum class CardBrand(
    val code: String,
    val displayName: String,
    @DrawableRes val icon: Int,
    private val prefixes: Set<String> = emptySet()
) {
    Visa(
        "visa",
        "Visa",
        R.drawable.airwallex_ic_visa,
        prefixes = setOf("4")
    ),
    MasterCard(
        "mastercard",
        "MasterCard",
        R.drawable.airwallex_ic_mastercard,
        prefixes = setOf(
            "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224",
            "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720",
            "50", "51", "52", "53", "54", "55", "67"
        )
    ),
    Unknown(
        "unknown",
        "Unknown",
        R.drawable.airwallex_ic_card_default
    );

    companion object {
        fun fromCardNumber(cardNumber: String?): CardBrand {
            return values()
                .firstOrNull { cardBrand ->
                    cardBrand.prefixes
                        .takeIf {
                            it.isNotEmpty()
                        }?.any {
                            cardNumber?.startsWith(it) == true
                        } == true
                } ?: Unknown
        }
    }
}
