package com.airwallex.android.view

import androidx.annotation.IntRange
import androidx.annotation.Size
import java.util.*

internal object ExpiryDateUtils {

    private const val MAX_VALID_YEAR = 9980

    internal fun isValidMonth(monthString: String?): Boolean {
        return try {
            monthString?.toInt() in 1..12
        } catch (numEx: NumberFormatException) {
            false
        }
    }

    internal fun separateDateParts(@Size(max = 4) expiryInput: String): Array<String> {
        return if (expiryInput.length >= 2) {
            listOf(
                expiryInput.substring(0, 2),
                expiryInput.substring(2)
            ).toTypedArray()
        } else {
            listOf(expiryInput, "").toTypedArray()
        }
    }

    internal fun isExpiryDateValid(expiryMonth: Int, expiryYear: Int): Boolean {
        return isExpiryDateValid(expiryMonth, expiryYear, Calendar.getInstance())
    }

    private fun isExpiryDateValid(expiryMonth: Int, expiryYear: Int, calendar: Calendar): Boolean {
        if (expiryMonth < 1 || expiryMonth > 12) {
            return false
        }

        if (expiryYear < 0 || expiryYear > MAX_VALID_YEAR) {
            return false
        }

        val currentYear = calendar.get(Calendar.YEAR)
        return when {
            expiryYear < currentYear -> false
            expiryYear > currentYear -> true
            else -> {
                val readableMonth = calendar.get(Calendar.MONTH) + 1
                expiryMonth >= readableMonth
            }
        }
    }

    @IntRange(from = 1000, to = 9999)
    internal fun convertTwoDigitYearToFour(@IntRange(from = 0, to = 99) inputYear: Int): Int {
        return convertTwoDigitYearToFour(inputYear, Calendar.getInstance())
    }

    @IntRange(from = 1000, to = 9999)
    private fun convertTwoDigitYearToFour(
        @IntRange(from = 0, to = 99) inputYear: Int,
        calendar: Calendar
    ): Int {
        val year = calendar.get(Calendar.YEAR)
        var centuryBase = year / 100
        if (year % 100 > 80 && inputYear < 20) {
            centuryBase++
        } else if (year % 100 < 20 && inputYear > 80) {
            centuryBase--
        }
        return centuryBase * 100 + inputYear
    }
}
