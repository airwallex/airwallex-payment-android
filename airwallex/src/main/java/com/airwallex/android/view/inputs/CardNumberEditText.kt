package com.airwallex.android.view.inputs

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.util.CardUtils
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
        // Prefix that identifies card brand
        private const val BRAND_PREFIX_LENGTH = 4

        private val INPUT_MAX_LENGTH =
            CardBrand.Unknown.spacingPattern.size - 1 + CardUtils.maxCardNumberLength
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
        cardBrand: CardBrand,
        newLength: Int,
        editActionStart: Int,
        editActionAddition: Int
    ): Int {
        var gapsJumped = 0
        var skipBack = false
        CardUtils.getSpacePositions(cardBrand).forEach { gap ->
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
            private var formattedNumber: String? = null

            private var currentBrand: CardBrand = CardBrand.Unknown

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

                if (start > CardUtils.maxCardNumberLength) {
                    // no need to do formatting if we're past all of the spaces.
                    return
                }

                if (start < BRAND_PREFIX_LENGTH) {
                    val brand =
                        CardUtils.getPossibleCardBrand(cardNumber = inputText, shouldNormalize = true)
                    currentBrand = brand
                    brandChangeCallback.invoke(brand)
                }

                val spaceLessNumber = CardUtils.removeSpacesAndHyphens(inputText) ?: return
                val formatNumber = createFormattedNumber(spaceLessNumber, currentBrand)

                this.cursorPosition = updateSelectionIndex(
                    cardBrand = currentBrand,
                    newLength = formatNumber.length,
                    editActionStart = latestChangeStart,
                    editActionAddition = latestInsertionSize
                )
                this.formattedNumber = formatNumber
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

    /**
     * Format card number by following the spacing pattern of its card brand
     */
    private fun createFormattedNumber(cardNumber: String, cardBrand: CardBrand): String {
        var formattedNumber = cardNumber
        val spacePositions = CardUtils.getSpacePositions(cardBrand)
        spacePositions.forEach { index ->
            if (formattedNumber.length > index) {
                formattedNumber = formattedNumber.replaceRange(index, index, " ")
            }
        }

        return formattedNumber
    }
}
