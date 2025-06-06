package com.airwallex.android.view.util

import com.airwallex.android.core.CardBrand

fun String.isValidCvc(brand: CardBrand): Boolean {
    return try {
        this.toInt()
        when (brand) {
            CardBrand.Amex -> this.length == 4
            else -> this.length == 3
        }
    } catch (numEx: NumberFormatException) {
        false
    }
}