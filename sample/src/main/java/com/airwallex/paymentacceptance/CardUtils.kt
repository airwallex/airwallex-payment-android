package com.airwallex.paymentacceptance

import android.annotation.SuppressLint
import com.airwallex.android.model.PaymentMethod
import com.airwallex.paymentacceptance.view.CardBrand.Companion.fromCardNumber

object CardUtils {

    private const val LENGTH_CARD = 16

    @JvmStatic
    fun isValidCardNumber(cardNumber: String?): Boolean {
        val normalizedNumber = removeSpacesAndHyphens(cardNumber)
        return isValidLuhnNumber(normalizedNumber) && isValidCardLength(normalizedNumber)
    }

    private fun isValidLuhnNumber(cardNumber: String?): Boolean {
        if (cardNumber == null) {
            return false
        }

        var isOdd = true
        var sum = 0

        for (index in cardNumber.length - 1 downTo 0) {
            val c = cardNumber[index]
            if (!Character.isDigit(c)) {
                return false
            }

            var digitInteger = Character.getNumericValue(c)
            isOdd = !isOdd

            if (isOdd) {
                digitInteger *= 2
            }

            if (digitInteger > 9) {
                digitInteger -= 9
            }

            sum += digitInteger
        }

        return sum % 10 == 0
    }

    private fun isValidCardLength(cardNumber: String?): Boolean {
        return cardNumber != null && isValidCardLength(
            cardNumber,
            getPossibleCardBrand(cardNumber, false)
        )
    }

    private fun isValidCardLength(
        cardNumber: String?,
        @PaymentMethod.Card.CardBrand cardBrand: String
    ): Boolean {
        if (cardNumber == null || PaymentMethod.Card.CardBrand.UNKNOWN == cardBrand) {
            return false
        }
        return cardNumber.length == LENGTH_CARD
    }

    fun getPossibleCardBrand(cardNumber: String?): String {
        return getPossibleCardBrand(cardNumber = cardNumber, shouldNormalize = true)
    }

    @SuppressLint("WrongConstant")
    @PaymentMethod.Card.CardBrand
    private fun getPossibleCardBrand(cardNumber: String?, shouldNormalize: Boolean): String {
        if (cardNumber.isNullOrBlank()) {
            return PaymentMethod.Card.CardBrand.UNKNOWN
        }

        val spacelessCardNumber =
            if (shouldNormalize) {
                removeSpacesAndHyphens(cardNumber)
            } else {
                cardNumber
            }

        return fromCardNumber(spacelessCardNumber)?.displayName ?: ""
    }

    fun removeSpacesAndHyphens(cardNumberWithSpaces: String?): String? {
        return cardNumberWithSpaces
            .takeUnless { it.isNullOrBlank() }
            ?.replace("\\s|-".toRegex(), "")
    }
}
