package com.airwallex.android.view

import com.airwallex.android.view.CardBrand.Companion.fromCardNumber

internal object CardUtils {

    /**
     * The valid card length
     */
    private const val VALID_CARD_LENGTH = 16

    /**
     * Check if card number is valid
     */
    internal fun isValidCardNumber(cardNumber: String?): Boolean {
        val normalizedNumber = removeSpacesAndHyphens(cardNumber)
        return isValidLuhnNumber(normalizedNumber) && isValidCardLength(normalizedNumber)
    }

    /**
     * Check if number is valid luhn number
     */
    internal fun isValidLuhnNumber(number: String?): Boolean {
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

    internal fun isValidCardLength(cardNumber: String?): Boolean {
        return cardNumber != null && isValidCardLength(
            cardNumber,
            getPossibleCardBrand(cardNumber, false)
        )
    }

    /**
     * Check if card length is valid
     */
    private fun isValidCardLength(
        cardNumber: String?,
        cardBrand: CardBrand?
    ): Boolean {
        if (cardNumber == null || cardBrand == null) {
            return false
        }
        return cardNumber.length == VALID_CARD_LENGTH
    }

    /**
     * Get all possible card brands of the card number
     *
     *
     * @param cardNumber the credit card number
     * @return [CardBrand] of the card number
     */
    internal fun getPossibleCardBrand(cardNumber: String?, shouldNormalize: Boolean): CardBrand {
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

    /**
     * Remove all spaces and hyphens of the card number
     */
    internal fun removeSpacesAndHyphens(cardNumberWithSpaces: String?): String? {
        return cardNumberWithSpaces
            .takeUnless { it.isNullOrBlank() }
            ?.replace("\\s|-".toRegex(), "")
    }
}
