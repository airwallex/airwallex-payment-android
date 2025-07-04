package com.airwallex.android.view.util

import androidx.annotation.Size
import java.util.Calendar
import java.util.TimeZone

object ExpiryDateUtils {

    private const val MAX_VALID_YEAR = 99
    const val VALID_INPUT_LENGTH = 5

    fun isValidMonth(monthString: String?): Boolean {
        return try {
            monthString?.toInt() in 1..12
        } catch (numEx: NumberFormatException) {
            false
        }
    }

    fun separateDateInput(@Size(max = 4) expiryInput: String): Array<String> {
        return if (expiryInput.length >= 2) {
            listOf(
                expiryInput.substring(0, 2),
                expiryInput.substring(2)
            ).toTypedArray()
        } else {
            listOf(expiryInput, "").toTypedArray()
        }
    }

    fun formatExpiryDate(rawInput: String): String {
        val formattedDateBuilder = StringBuilder()
        when (rawInput.length) {
            0 -> return ""
            1 -> {
                when (rawInput) {
                    "0",
                    "1" -> formattedDateBuilder.append(rawInput)
                    else -> formattedDateBuilder.append("0$rawInput/")
                }
            }
            2 -> formattedDateBuilder.append("$rawInput/")
            else -> {
                if (rawInput[2] == '/') {
                    formattedDateBuilder.append(rawInput)
                } else {
                    formattedDateBuilder.append("${rawInput.substring(0, 2)}/${rawInput.substring(2)}")
                }
            }
        }
        return formattedDateBuilder.toString()
    }

    fun formatExpiryDateWhenDeleting(rawInput: String): String {
        val formattedDateBuilder = StringBuilder()
        when (rawInput.length) {
            3 -> {
                if (rawInput[2] == '/') {
                    formattedDateBuilder.append(rawInput.substring(0, 2))
                } else {
                    formattedDateBuilder.append(rawInput)
                }
            }
            else -> formattedDateBuilder.append(rawInput)
        }
        return formattedDateBuilder.toString()
    }

    fun isValidExpiryDate(rawInput: String): Boolean {
        if (rawInput.length != VALID_INPUT_LENGTH) return false

        val dateParts = separateDateInput(rawInput.replace("/", ""))
        if (dateParts.any { it.length != 2 }) return false

        val month = dateParts[0]
        val year = dateParts[1]

        return isValidMonth(month) && try {
            isExpiryDateValid(month.toInt(), year.toInt())
        } catch (e: NumberFormatException) {
            false
        }
    }

    internal fun isExpiryDateValid(expiryMonth: Int, expiryYear: Int): Boolean {
        return isExpiryDateValid(
            expiryMonth,
            expiryYear,
            Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        )
    }

    internal fun isExpiryDateValid(expiryMonth: Int, expiryYear: Int, calendar: Calendar): Boolean {
        if (expiryMonth < 1 || expiryMonth > 12) {
            return false
        }

        if (expiryYear < 0 || expiryYear > MAX_VALID_YEAR) {
            return false
        }

        val currentYear = calendar.get(Calendar.YEAR) % 100
        return when {
            expiryYear < currentYear -> false
            expiryYear > currentYear -> true
            else -> {
                val readableMonth = calendar.get(Calendar.MONTH) + 1
                expiryMonth >= readableMonth
            }
        }
    }
}

fun String.createExpiryMonthAndYear(): Pair<Int, Int>? {
    val rawNumericInput = this.replace("/".toRegex(), "")
    val dateFields = ExpiryDateUtils.separateDateInput(rawNumericInput)

    return try {
        Pair(
            dateFields[0].toInt(),
            "20${dateFields[1]}".toInt()
        )
    } catch (numEx: NumberFormatException) {
        null
    }
}