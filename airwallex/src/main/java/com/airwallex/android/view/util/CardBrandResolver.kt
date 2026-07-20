package com.airwallex.android.view.util

import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.PaymentMethod

/**
 * Resolves the brand name to display for a saved card. The backend labels Maestro cards as
 * "mastercard", so when the stored brand is Mastercard and the BIN identifies a Maestro card,
 * surface Maestro instead. Every other brand is returned unchanged.
 */
fun PaymentMethod.Card.resolvedBrandName(): String? {
    val brandName = brand ?: return null
    if (!brandName.equals(CardBrand.MasterCard.type, ignoreCase = true)) return brandName
    return if (CardBrand.fromCardNumber(bin) == CardBrand.Maestro) {
        CardBrand.Maestro.type
    } else {
        brandName
    }
}
