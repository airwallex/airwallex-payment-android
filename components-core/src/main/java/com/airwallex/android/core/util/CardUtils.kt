package com.airwallex.android.core.util

import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.CardBrand.Companion.fromCardNumber

object CardUtils {
    /**
     * The maximum length of a possible card number
     */
    val maxCardNumberLength = CardBrand.values().maxOf { it.lengths.max() }

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
    fun isValidLuhnNumber(number: String?): Boolean {
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
    fun isValidCardLength(cardNumber: String?, shouldNormalize: Boolean = false): Boolean {
        if (cardNumber == null) {
            return false
        }

        val normalizeCardNumber =
            if (shouldNormalize) {
                removeSpacesAndHyphens(cardNumber)
            } else {
                cardNumber
            }

        return when (val brand = getPossibleCardBrand(normalizeCardNumber, false)) {
            CardBrand.Unknown -> false
            else -> normalizeCardNumber?.length in brand.lengths
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
        val spacePositions = mutableSetOf<Int>()
        var spaceIndex = 0
        var lastPosition = 0
        while (spaceIndex < cardBrand.spacingPattern.size - 1) {
            lastPosition += cardBrand.spacingPattern[spaceIndex]
            if (spaceIndex > 0) {
                lastPosition++
            }
            spacePositions.add(lastPosition)
            spaceIndex++
        }
        return spacePositions
    }

    fun formatCardNumber(input: String, brand: CardBrand, onComplete: ((String) -> Unit)): String {
        val maxCardLength = brand.lengths.max() + brand.spacingPattern.size - 1
        var formattedText = input.take(maxCardLength)
        val spacePositions = getSpacePositions(brand)

        spacePositions.forEach { index ->
            if (formattedText.length > index && formattedText[index] != ' ') {
                formattedText = formattedText.substring(0, index) + ' ' + formattedText.substring(index)
            }
        }

        if (formattedText.length == maxCardLength) {
            onComplete(formattedText)
        }

        return formattedText
    }
}
