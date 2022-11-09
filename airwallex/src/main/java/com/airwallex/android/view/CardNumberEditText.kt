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
        // Space index
        private val SPACE_CARD_SET = setOf(4, 9, 14)
        private val INPUT_MAX_LENGTH = SPACE_CARD_SET.count() + CardUtils.VALID_NORMAL_CARD_LENGTH
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
     * Card number validation message callback
     */
    internal var validationMessageCallback: (String) -> String? = { null }

    /**
     * Card Brand changed callback
     */
    internal var brandChangeCallback: (CardBrand) -> Unit = {}

    /**
     * Error callback when the card is invalid
     */
    internal var errorCallback: (errorMessage: String?) -> Unit = {}

    /**
     * Return the card number if valid, otherwise null.
     */
    internal val cardNumber: String?
        get() = if (validationMessage == null) {
            CardUtils.removeSpacesAndHyphens(text?.toString().orEmpty())
        } else {
            null
        }

    /**
     * Completion callback when a valid card has been entered
     */
    internal var completionCallback: () -> Unit = {}

    /**
     * Validation message of the card number input
     */
    internal var validationMessage: String? = resources.getString(R.string.airwallex_empty_card_number)

    /**
     * We should ignore TextChanged event when setText, avoid duplicate
     */
    private var ignoreTextChanges = false

    init {
        maxLines = 1
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(INPUT_MAX_LENGTH))
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

                val cardNumber = text?.toString().orEmpty()
                if (CardUtils.isValidCardLength(cardNumber, true) || cardNumber.length == INPUT_MAX_LENGTH) {
                    val previousMessage = validationMessage
                    validationMessage = validationMessageCallback(cardNumber)
                    errorCallback(validationMessage)
                    if (previousMessage != null && validationMessage == null) {
                        completionCallback()
                    }
                } else {
                    validationMessage = validationMessageCallback(cardNumber)
                    errorCallback(null)
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
