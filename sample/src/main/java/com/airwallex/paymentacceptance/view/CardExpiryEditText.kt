package com.airwallex.paymentacceptance.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import com.airwallex.paymentacceptance.R
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.min

class CardExpiryEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private companion object {
        private const val INVALID_INPUT = -1
        private const val MAX_INPUT_LENGTH = 5
    }

    internal var errorCallback: (showError: Boolean) -> Unit = {}

    init {
        setHint(R.string.expires_hint)
        maxLines = 1
        filters = arrayOf(InputFilter.LengthFilter(MAX_INPUT_LENGTH))

        inputType = InputType.TYPE_CLASS_DATETIME

        listenForTextChanges()
    }

    internal var completionCallback: () -> Unit = {}

    var isDateValid: Boolean = false

    val validDateFields: Pair<Int, Int>?
        get() {
            val rawInput = text?.toString().takeIf { isDateValid } ?: return null
            val rawNumericInput = rawInput.replace("/".toRegex(), "")
            val dateFields = DateUtils.separateDateStringParts(rawNumericInput)

            return try {
                Pair(
                    dateFields[0].toInt(),
                    DateUtils.convertTwoDigitYearToFour(dateFields[1].toInt())
                )
            } catch (numEx: NumberFormatException) {
                null
            }
        }

    private fun listenForTextChanges() {
        addTextChangedListener(object : TextWatcher {
            private var ignoreChanges = false
            private var latestChangeStart: Int = 0
            private var latestInsertionSize: Int = 0
            private var parts: Array<String> = arrayOf("", "")

            private var newCursorPosition: Int? = null
            private var formattedDate: String? = null

            // two-digit month
            val month: String
                get() {
                    return parts[0]
                }

            // two-digit year
            val year: String
                get() {
                    return parts[1]
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

                val inputText = s?.toString().orEmpty()
                var rawNumericInput = inputText.replace("/".toRegex(), "")

                if (rawNumericInput.length == 1 && latestChangeStart == 0 &&
                    latestInsertionSize == 1
                ) {
                    val first = rawNumericInput[0]
                    if (!(first == '0' || first == '1')) {
                        // If the first digit typed isn't 0 or 1, then it can't be a valid
                        // two-digit month. Hence, we assume the user is inputting a one-digit
                        // month. We bump it to the preferred input, so "4" becomes "04", which
                        // later in this method goes to "04/".
                        rawNumericInput = "0$rawNumericInput"
                        latestInsertionSize++
                    }
                } else if (rawNumericInput.length == 2 &&
                    latestChangeStart == 2 &&
                    latestInsertionSize == 0
                ) {
                    // This allows us to delete past the separator, so that if a user presses
                    // delete when the current string is "12/", the resulting string is "1," since
                    // we pretend that the "/" isn't really there. The case that we also want,
                    // where "12/3" + DEL => "12" is handled elsewhere.
                    rawNumericInput = rawNumericInput.substring(0, 1)
                }

                // Date input is MM/YY, so the separated parts will be {MM, YY}
                parts = DateUtils.separateDateStringParts(rawNumericInput)

                if (!DateUtils.isValidMonth(month)) {
                    inErrorState = true
                }

                val formattedDateBuilder = StringBuilder()
                    .append(month)

                if (month.length == 2 && latestInsertionSize > 0 &&
                    !inErrorState || rawNumericInput.length > 2
                ) {
                    formattedDateBuilder.append("/")
                }

                formattedDateBuilder.append(year)

                val formattedDate = formattedDateBuilder.toString()
                this.newCursorPosition = updateSelectionIndex(
                    formattedDate.length,
                    latestChangeStart, latestInsertionSize, MAX_INPUT_LENGTH
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

                // Note: we want to show an error state if the month is invalid or the
                // final, complete date is in the past. We don't want to show an error state for
                // incomplete entries.

                // This covers the case where the user has entered a month of 15, for instance.
                var shouldShowError = month.length == 2 &&
                        !DateUtils.isValidMonth(month)

                // Note that we have to check the parts array because afterTextChanged has odd
                // behavior when it comes to pasting, where a paste of "1212" triggers this
                // function for the strings "12/12" (what it actually becomes) and "1212",
                // so we might not be properly catching an error state.
                if (month.length == 2 && year.length == 2) {
                    val wasComplete = isDateValid
                    updateInputValues(month, year)
                    // Here, we have a complete date, so if we've made an invalid one, we want
                    // to show an error.
                    shouldShowError = !isDateValid
                    if (!wasComplete && isDateValid) {
                        completionCallback()
                    }
                } else {
                    isDateValid = false
                }

                errorCallback.invoke(shouldShowError)

                formattedDate = null
                newCursorPosition = null
            }
        })
    }

    private fun updateSelectionIndex(
        newLength: Int,
        editActionStart: Int,
        editActionAddition: Int,
        maxInputLength: Int
    ): Int {
        val gapsJumped =
            if (editActionStart <= 2 && editActionStart + editActionAddition >= 2) {
                1
            } else {
                0
            }

        // editActionAddition can only be 0 if we are deleting,
        // so we need to check whether or not to skip backwards one space
        val skipBack = editActionAddition == 0 && editActionStart == 3

        var newPosition: Int = editActionStart + editActionAddition + gapsJumped
        if (skipBack && newPosition > 0) {
            newPosition--
        }
        val untruncatedPosition = if (newPosition <= newLength) newPosition else newLength
        return min(maxInputLength, untruncatedPosition)
    }

    private fun updateInputValues(month: String, year: String) {
        val inputMonth: Int = if (month.length != 2) {
            INVALID_INPUT
        } else {
            try {
                month.toInt()
            } catch (numEx: NumberFormatException) {
                INVALID_INPUT
            }
        }

        val inputYear: Int = if (year.length != 2) {
            INVALID_INPUT
        } else {
            try {
                DateUtils.convertTwoDigitYearToFour(year.toInt())
            } catch (numEx: NumberFormatException) {
                INVALID_INPUT
            }
        }

        isDateValid = DateUtils.isExpiryDataValid(inputMonth, inputYear)
    }
}
