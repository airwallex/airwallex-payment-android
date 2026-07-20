package com.airwallex.android.core.model

import com.airwallex.android.core.CardBrand

/**
 * Maestro is a debit sub-brand of Mastercard that the backend does not return as a separate
 * card scheme, so append it whenever Mastercard is supported and Maestro is not already present.
 */
fun List<CardScheme>.withMaestroIfMasterCard(): List<CardScheme> {
    val hasMasterCard = any { it.name.equals(CardBrand.MasterCard.type, ignoreCase = true) }
    val hasMaestro = any { it.name.equals(CardBrand.Maestro.type, ignoreCase = true) }
    return if (hasMasterCard && !hasMaestro) {
        this + CardScheme(CardBrand.Maestro.type)
    } else {
        this
    }
}
