package com.airwallex.android.view

import androidx.annotation.IntRange
import androidx.annotation.Size
import androidx.annotation.VisibleForTesting
import java.util.*

internal object DateUtils {

    private const val MAX_VALID_YEAR = 9980

    /**
     * Checks to see if the string input represents a valid month.
     *
     * @param monthString user input representing the month
     * @return `true` if the string is a number between "01" and "12" inclusive,
     * `false` otherwise
     */
    @JvmStatic
    fun isValidMonth(monthString: String?): Boolean {
        return try {
            monthString?.toInt() in 1..12
        } catch (numEx: NumberFormatException) {
            false
        }
    }

    /**
     * Separates raw string input of the format MMYY into a "month" group and a "year" group.
     * Either or both of these may be incomplete. This method does not check to see if the input
     * is valid.
     *
     * @param expiryInput up to four characters of user input
     * @return a length-2 array containing the first two characters in the 0 index, and the last
     * two characters in the 1 index. "123" gets split into {"12" , "3"}, and "1" becomes {"1", ""}.
     */
    @Size(2)
    @JvmStatic
    fun separateDateStringParts(@Size(max = 4) expiryInput: String): Array<String> {
        return if (expiryInput.length >= 2) {
            listOf(
                expiryInput.substring(0, 2),
                expiryInput.substring(2)
            ).toTypedArray()
        } else {
            listOf(expiryInput, "").toTypedArray()
        }
    }

    /**
     * Checks whether or not the input month and year has yet expired.
     *
     * @param expiryMonth An integer representing a month. Only values 1-12 are valid,
     * but this is called by user input, so we have to check outside that range.
     * @param expiryYear An integer representing the full year (2017, not 17). Only positive values
     * are valid, but this is called by user input, so we have to check outside
     * for otherwise nonsensical dates. This code cannot validate years greater
     * than [9980][MAX_VALID_YEAR] because of how we parse years in [convertTwoDigitYearToFour].
     * @return `true` if the current month and year is the same as or later than the input
     * month and year, `false` otherwise. Note that some cards expire on the first of the
     * month, but we don't validate that here.
     */
    @JvmStatic
    fun isExpiryDataValid(expiryMonth: Int, expiryYear: Int): Boolean {
        return isExpiryDataValid(expiryMonth, expiryYear, Calendar.getInstance())
    }

    @VisibleForTesting
    @JvmStatic
    fun isExpiryDataValid(expiryMonth: Int, expiryYear: Int, calendar: Calendar): Boolean {
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
            else -> { // the card expires this year
                val readableMonth = calendar.get(Calendar.MONTH) + 1
                expiryMonth >= readableMonth
            }
        }
    }

    /**
     * Converts a two-digit input year to a four-digit year. As the current calendar year
     * approaches a century, we assume small values to mean the next century. For instance, if
     * the current year is 2090, and the input value is "18", the user probably means 2118,
     * not 2018. However, in 2017, the input "18" probably means 2018. This code should be
     * updated before the year 9981.
     *
     * @param inputYear a two-digit integer, between 0 and 99, inclusive
     * @return a four-digit year
     */
    @IntRange(from = 1000, to = 9999)
    @JvmStatic
    fun convertTwoDigitYearToFour(@IntRange(from = 0, to = 99) inputYear: Int): Int {
        return convertTwoDigitYearToFour(inputYear, Calendar.getInstance())
    }

    @VisibleForTesting
    @IntRange(from = 1000, to = 9999)
    @JvmStatic
    fun convertTwoDigitYearToFour(
        @IntRange(from = 0, to = 99) inputYear: Int,
        calendar: Calendar
    ): Int {
        val year = calendar.get(Calendar.YEAR)
        // Intentional integer division
        var centuryBase = year / 100
        if (year % 100 > 80 && inputYear < 20) {
            centuryBase++
        } else if (year % 100 < 20 && inputYear > 80) {
            centuryBase--
        }
        return centuryBase * 100 + inputYear
    }
}
