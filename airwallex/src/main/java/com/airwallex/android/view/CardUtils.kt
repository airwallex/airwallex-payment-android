package com.airwallex.android.view

import com.airwallex.android.view.CardBrand.Companion.fromCardNumber

object CardUtils {
    /**
     * The valid card length
     */
    const val VALID_NORMAL_CARD_LENGTH = 16
    private const val VALID_AMEX_CARD_LENGTH = 15

    /**
     * Check if card number is valid
     */
    fun isValidCardNumber(cardNumber: String?): Boolean {
        val normalizedNumber = removeSpacesAndHyphens(cardNumber)
        return isValidLuhnNumber(normalizedNumber) && isValidCardLength(normalizedNumber)
    }

    /**
     * Check if number is a valid luhn number
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

    /**
     * Check if card length is valid
     */
    internal fun isValidCardLength(cardNumber: String?, shouldNormalize: Boolean = false): Boolean {
        if (cardNumber == null) {
            return false
        }

        val normalizeCardNumber =
            if (shouldNormalize) {
                removeSpacesAndHyphens(cardNumber)
            } else {
                cardNumber
            }

        return when (getPossibleCardBrand(normalizeCardNumber, false)) {
            CardBrand.Amex -> normalizeCardNumber?.length == VALID_AMEX_CARD_LENGTH
            CardBrand.Unknown -> false
            else -> normalizeCardNumber?.length == VALID_NORMAL_CARD_LENGTH
        }
    }

    /**
     * Get all possible card brands of the card number
     *
     * @param cardNumber the credit card number
     * @return [CardBrand] of the card number
     */
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

    /**
     * Remove all spaces and hyphens of the card number
     */
    fun removeSpacesAndHyphens(cardNumberWithSpaces: String?): String? {
        return cardNumberWithSpaces
            .takeUnless { it.isNullOrBlank() }
            ?.replace("\\s|-".toRegex(), "")
    }

    /**
     * Get space position by spacing pattern of the card brand
     * e.g. spacing pattern 4-4-4-4 is correlated to space position 4-9-14
     */
    fun getSpacePositions(cardBrand: CardBrand): Set<Int> {
        val spacePostions = mutableSetOf<Int>()
        var spaceIndex = 0
        var lastPosition = 0
        while (spaceIndex < cardBrand.spacingPattern.size - 1) {
            lastPosition += cardBrand.spacingPattern[spaceIndex]
            if (spaceIndex > 0) {
                lastPosition++
            }
            spacePostions.add(lastPosition)
            spaceIndex++
        }
        return spacePostions
    }
}
