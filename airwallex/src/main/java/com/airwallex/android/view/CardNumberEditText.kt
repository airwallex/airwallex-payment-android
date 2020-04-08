package com.airwallex.android.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import com.airwallex.android.R
import com.google.android.material.textfield.TextInputEditText

/**
 * A [TextInputEditText] to format the credit card number
 */
internal class CardNumberEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    internal companion object {
        private const val MAX_CARD_LENGTH = 19

        private val SPACE_CARD_SET = setOf(4, 9, 14)

        /**
         * Use spaces to format credit card number
         */
        internal fun createFormattedNumber(cardParts: Array<String?>): String {
            return cardParts
                .takeWhile { it != null }
                .joinToString(" ")
        }
    }

    /**
     * Card Brand changed callback
     */
    internal var brandChangeCallback: (CardBrand) -> Unit = {}

    /**
     * Error callback when the card is invalid
     */
    internal var errorCallback: (showError: Boolean) -> Unit = {}

    /**
     * Return the card number if valid, otherwise null.
     */
    internal val cardNumber: String?
        get() = if (isCardNumberValid) {
            CardUtils.removeSpacesAndHyphens(text?.toString().orEmpty())
        } else {
            null
        }

    /**
     * Completion callback when a valid card has been entered
     */
    internal var completionCallback: () -> Unit = {}

    /**
     * Check if the card number is legal
     */
    internal var isCardNumberValid: Boolean = false

    private var ignoreTextChanges = false

    init {
        setHint(R.string.card_number_hint)
        maxLines = 1
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_CARD_LENGTH))
        inputType = InputType.TYPE_CLASS_NUMBER

        listenForTextChanges()
    }

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
            private var formatNumber: String? = null

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
                if (start < 4) {
                    updateCardBrandFromNumber(inputText)
                }

                if (start > 16) {
                    // no need to do formatting if we're past all of the spaces.
                    return
                }

                val spaceLessNumber = CardUtils.removeSpacesAndHyphens(inputText) ?: return
                val formatNumber =
                    createFormattedNumber(separateCardNumberGroups(spaceLessNumber))

                this.cursorPosition = updateSelectionIndex(
                    newLength = formatNumber.length,
                    editActionStart = latestChangeStart,
                    editActionAddition = latestInsertionSize
                )
                this.formatNumber = formatNumber
            }

            override fun afterTextChanged(s: Editable?) {
                if (ignoreTextChanges) {
                    return
                }

                ignoreTextChanges = true
                if (formatNumber != null) {
                    setText(formatNumber)
                    cursorPosition?.let {
                        setSelection(it)
                    }
                }
                formatNumber = null
                cursorPosition = null
                ignoreTextChanges = false

                val fieldText = text?.toString().orEmpty()
                if (fieldText.length == MAX_CARD_LENGTH) {
                    val before = isCardNumberValid
                    isCardNumberValid = CardUtils.isValidCardNumber(fieldText)
                    errorCallback.invoke(!isCardNumberValid)
                    if (!before && isCardNumberValid) {
                        completionCallback()
                    }
                } else {
                    isCardNumberValid = CardUtils.isValidCardNumber(fieldText)
                    errorCallback.invoke(false)
                }
            }
        })
    }

    private fun updateCardBrandFromNumber(partialNumber: String) {
        val brand =
            CardUtils.getPossibleCardBrand(cardNumber = partialNumber, shouldNormalize = true)
        brandChangeCallback.invoke(brand)
    }

    private fun separateCardNumberGroups(cardNumber: String): Array<String?> {
        val numberGroups = arrayOfNulls<String?>(4)
        var i = 0
        var previousStart = 0
        while ((i + 1) * 4 < cardNumber.length) {
            val group = cardNumber.substring(previousStart, (i + 1) * 4)
            numberGroups[i] = group
            previousStart = (i + 1) * 4
            i++
        }
        numberGroups[i] = cardNumber.substring(previousStart)

        return numberGroups
    }
}
