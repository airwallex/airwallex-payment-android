package com.airwallex.android.view.util

import com.airwallex.android.core.CardBrand

fun String.isValidCvc(brand: CardBrand): Boolean {
    return try {
        val cvc = this.toInt()
        when (brand) {
            CardBrand.Amex -> cvc.toString().length == 4
            else -> cvc.toString().length == 3
        }
    } catch (numEx: NumberFormatException) {
        false
    }
}