package com.airwallex.paymentacceptance

import com.airwallex.paymentacceptance.view.CardBrand
import com.airwallex.paymentacceptance.view.CardBrand.Companion.fromCardNumber

object CardUtils {

    private const val LENGTH_CARD = 16

    @JvmStatic
    fun isValidCardNumber(cardNumber: String?): Boolean {
        val normalizedNumber = removeSpacesAndHyphens(cardNumber)
        return isValidNumber(normalizedNumber) && isValidCardLength(normalizedNumber)
    }

    private fun isValidNumber(number: String?): Boolean {
        if (number == null) {
            return false
        }

        var isOdd = true
        var sum = 0

        for (index in number.length - 1 downTo 0) {
            val c = number[index]
            if (!Character.isDigit(c)) {
                return false
            }

            isOdd = !isOdd

            var digitInteger = Character.getNumericValue(c)
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
        cardBrand: CardBrand?
    ): Boolean {
        if (cardNumber == null || cardBrand == null) {
            return false
        }
        return cardNumber.length == LENGTH_CARD
    }

    fun getPossibleCardBrand(cardNumber: String?, shouldNormalize: Boolean): CardBrand {
        if (cardNumber.isNullOrBlank()) {
            return CardBrand.Unknown
        }

        val normalizeCardNumber =
            if (shouldNormalize) {
                removeSpacesAndHyphens(cardNumber)
            } else {
                cardNumber
            }

        return fromCardNumber(normalizeCardNumber)
    }

    fun removeSpacesAndHyphens(cardNumberWithSpaces: String?): String? {
        return cardNumberWithSpaces
            .takeUnless { it.isNullOrBlank() }
            ?.replace("\\s|-".toRegex(), "")
    }
}
