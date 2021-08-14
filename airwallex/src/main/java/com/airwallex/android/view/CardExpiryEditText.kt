package com.airwallex.android.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import com.airwallex.android.R
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.min

/**
 * A [TextInputEditText] to format the credit card expiry date
 */
internal class CardExpiryEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private companion object {
        private const val INVALID_INPUT = -1
        private const val VALID_INPUT_LENGTH = 7
    }

    internal var errorCallback: (showError: Boolean) -> Unit = {}

    internal var completionCallback: () -> Unit = {}

    internal var isDateValid: Boolean = false

    internal val validDateFields: Pair<Int, Int>?
        get() {
            val rawInput = text?.toString().takeIf { isDateValid } ?: return null
            val rawNumericInput = rawInput.replace("/".toRegex(), "")
            val dateFields = ExpiryDateUtils.separateDateInput(rawNumericInput)

            return try {
                Pair(
                    dateFields[0].toInt(),
                    dateFields[1].toInt()
                )
            } catch (numEx: NumberFormatException) {
                null
            }
        }

    init {
        setHint(R.string.airwallex_expires_hint)
        maxLines = 1
        filters = arrayOf(InputFilter.LengthFilter(VALID_INPUT_LENGTH))

        inputType = InputType.TYPE_CLASS_DATETIME

        listenForTextChanges()
    }

    private fun listenForTextChanges() {
        addTextChangedListener(object : TextWatcher {
            private var ignoreChanges = false
            private var latestChangeStart: Int = 0
            private var latestInsertionSize: Int = 0
            private var dateParts: Array<String> = arrayOf("", "")

            private var newCursorPosition: Int? = null
            private var formattedDate: String? = null

            // two-digit month
            val month: String
                get() {
                    return dateParts[0]
                }

            // four-digit year
            val year: String
                get() {
                    return dateParts[1]
                }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (ignoreChanges) {
                    return
                }
                latestChangeStart = start
                latestInsertionSize = after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ignoreChanges) {
                    return
                }

                var inErrorState = false
                var rawDateInput = s?.toString().orEmpty().replace("/".toRegex(), "")

                if (rawDateInput.length == 1 && latestChangeStart == 0 && latestInsertionSize == 1) {
                    val first = rawDateInput[0]
                    if (!(first == '0' || first == '1')) {
                        rawDateInput = "0$rawDateInput"
                        latestInsertionSize++
                    }
                } else if (rawDateInput.length == 2 && latestChangeStart == 2 && latestInsertionSize == 0) {
                    rawDateInput = rawDateInput.substring(0, 1)
                }

                dateParts = ExpiryDateUtils.separateDateInput(rawDateInput)

                if (!ExpiryDateUtils.isValidMonth(month)) {
                    inErrorState = true
                }

                val formattedDateBuilder = StringBuilder().append(month)
                if (month.length == 2 && latestInsertionSize > 0 && !inErrorState || rawDateInput.length > 2) {
                    formattedDateBuilder.append("/")
                }

                formattedDateBuilder.append(year)

                val formattedDate = formattedDateBuilder.toString()
                this.newCursorPosition = updateSelectionIndex(
                    formattedDate.length,
                    latestChangeStart,
                    latestInsertionSize
                )
                this.formattedDate = formattedDate
            }

            override fun afterTextChanged(s: Editable?) {
                if (ignoreChanges) {
                    return
                }
                ignoreChanges = true
                if (formattedDate != null) {
                    setText(formattedDate)
                    newCursorPosition?.let {
                        setSelection(it)
                    }
                }
                ignoreChanges = false
                var showError = month.length == 2 && !ExpiryDateUtils.isValidMonth(month)
                if (month.length == 2 && year.length == 4) {
                    val wasComplete = isDateValid
                    checkDateValid(month, year)
                    showError = !isDateValid
                    if (!wasComplete && isDateValid) {
                        completionCallback()
                    }
                } else {
                    isDateValid = false
                }

                errorCallback.invoke(showError)

                formattedDate = null
                newCursorPosition = null
            }
        })
    }

    private fun updateSelectionIndex(
        newLength: Int,
        editActionStart: Int,
        editActionAddition: Int
    ): Int {
        val gapsJumped =
            if (editActionStart <= 2 && editActionStart + editActionAddition >= 2) {
                1
            } else {
                0
            }
        val skipBack = editActionAddition == 0 && editActionStart == 3

        var newPosition: Int = editActionStart + editActionAddition + gapsJumped
        if (skipBack && newPosition > 0) {
            newPosition--
        }
        val unTruncatedPosition = if (newPosition <= newLength) newPosition else newLength
        return min(VALID_INPUT_LENGTH, unTruncatedPosition)
    }

    private fun checkDateValid(month: String, year: String) {
        val inputMonth: Int = if (month.length != 2) {
            INVALID_INPUT
        } else {
            try {
                month.toInt()
            } catch (numEx: NumberFormatException) {
                INVALID_INPUT
            }
        }

        val inputYear: Int = if (year.length != 4) {
            INVALID_INPUT
        } else {
            try {
                year.toInt()
            } catch (numEx: NumberFormatException) {
                INVALID_INPUT
            }
        }

        isDateValid = ExpiryDateUtils.isExpiryDateValid(inputMonth, inputYear)
    }
}
