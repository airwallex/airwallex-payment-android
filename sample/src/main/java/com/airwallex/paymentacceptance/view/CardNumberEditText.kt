package com.airwallex.paymentacceptance.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import com.airwallex.paymentacceptance.CardUtils
import com.airwallex.paymentacceptance.R

class CardNumberEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AirwallexEditText(context, attrs, defStyleAttr) {

    internal companion object {
        private const val MAX_CARD_LENGTH = 19

        private val SPACE_CARD_SET = setOf(4, 9, 14)

        @JvmSynthetic
        internal fun createFormattedNumber(cardParts: Array<String?>): String {
            return cardParts
                .takeWhile { it != null }
                .joinToString(" ")
        }
    }

    internal val cardNumber: String?
        get() = if (isCardNumberValid) {
            CardUtils.removeSpacesAndHyphens(fieldText)
        } else {
            null
        }

    internal var completionCallback: () -> Unit = {}

    private var isCardNumberValid: Boolean = false
    // When we format the card number, we need to ignore the text change event.
    private var ignoreTextChanges = false

    init {
        setHint(R.string.card_number_hint)
        maxLines = 1
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_CARD_LENGTH))
        inputType = InputType.TYPE_CLASS_NUMBER

        listenForTextChanges()
    }

    @JvmSynthetic
    internal fun updateSelectionIndex(
        newLength: Int,
        editActionStart: Int,
        editActionAddition: Int
    ): Int {
        var gapsJumped = 0
        var skipBack = false
        SPACE_CARD_SET.forEach { gap ->
            if (editActionStart <= gap && editActionStart + editActionAddition > gap) {
                gapsJumped++
            }

            // editActionAddition can only be 0 if we are deleting,
            // so we need to check whether or not to skip backwards one space
            if (editActionAddition == 0 && editActionStart == gap + 1) {
                skipBack = true
            }
        }

        var newPosition: Int = editActionStart + editActionAddition + gapsJumped
        if (skipBack && newPosition > 0) {
            newPosition--
        }

        return if (newPosition <= newLength) {
            newPosition
        } else {
            newLength
        }
    }

    private fun listenForTextChanges() {
        addTextChangedListener(object : TextWatcher {
            private var latestChangeStart: Int = 0
            private var latestInsertionSize: Int = 0

            private var cursorPosition: Int? = null
            private var formattedNumber: String? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!ignoreTextChanges) {
                    latestChangeStart = start
                    latestInsertionSize = after
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ignoreTextChanges) {
                    return
                }

                val inputText = s?.toString().orEmpty()
                if (start > 16) {
                    // no need to do formatting if we're past all of the spaces.
                    return
                }

                val spacelessNumber = CardUtils.removeSpacesAndHyphens(inputText) ?: return
                val formattedNumber =
                    createFormattedNumber(separateCardNumberGroups(spacelessNumber))

                this.cursorPosition = updateSelectionIndex(
                    newLength = formattedNumber.length,
                    editActionStart = latestChangeStart,
                    editActionAddition = latestInsertionSize
                )
                this.formattedNumber = formattedNumber
            }

            override fun afterTextChanged(s: Editable?) {
                if (ignoreTextChanges) {
                    return
                }

                ignoreTextChanges = true
                if (formattedNumber != null) {
                    setText(formattedNumber)
                    cursorPosition?.let {
                        setSelection(it)
                    }
                }
                formattedNumber = null
                cursorPosition = null
                ignoreTextChanges = false

                if (fieldText.length == MAX_CARD_LENGTH) {
                    val before = isCardNumberValid
                    isCardNumberValid = CardUtils.isValidCardNumber(fieldText)
                    shouldShowError = !isCardNumberValid
                    if (!before && isCardNumberValid) {
                        completionCallback()
                    }
                } else {
                    isCardNumberValid = CardUtils.isValidCardNumber(fieldText)
                    // Don't show errors if we aren't full-length.
                    shouldShowError = false
                }
            }
        })
    }

    fun separateCardNumberGroups(spacelessCardNumber: String): Array<String?> {
        val numberGroups = arrayOfNulls<String?>(4)
        var i = 0
        var previousStart = 0
        while ((i + 1) * 4 < spacelessCardNumber.length) {
            val group = spacelessCardNumber.substring(previousStart, (i + 1) * 4)
            numberGroups[i] = group
            previousStart = (i + 1) * 4
            i++
        }
        // Always stuff whatever is left into the next available array entry. This handles
        // incomplete numbers, full 16-digit numbers, and full 14-digit numbers
        numberGroups[i] = spacelessCardNumber.substring(previousStart)

        return numberGroups
    }
}
