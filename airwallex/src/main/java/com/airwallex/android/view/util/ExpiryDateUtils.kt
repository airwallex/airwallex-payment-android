package com.airwallex.android.view.util

import androidx.annotation.Size
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.Calendar
import java.util.TimeZone

object ExpiryDateUtils {

    private const val MAX_VALID_YEAR = 99
    const val VALID_INPUT_LENGTH = 5
    const val VALID_RAW_LENGTH = 4

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

    fun formatRawExpiryInput(rawDigits: String): String {
        val digits = rawDigits.filter { it.isDigit() }
        if (digits.isEmpty()) return ""
        if (digits.length == 1 && digits[0] != '0' && digits[0] != '1') {
            return "0$digits"
        }
        return digits
    }

    fun isValidExpiryDate(rawInput: String): Boolean {
        val digits = rawInput.replace("/", "")
        if (digits.length != VALID_RAW_LENGTH) return false

        val dateParts = separateDateInput(digits)
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

class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val out = buildString {
            raw.forEachIndexed { index, c ->
                if (index == 2) append('/')
                append(c)
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset <= 2) offset else offset + 1
            }
            override fun transformedToOriginal(offset: Int): Int {
                return if (offset <= 2) offset else offset - 1
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
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