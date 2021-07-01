package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R

/**
 * A [AirwallexTextInputLayout] to format the credit card expiry date, display errors and support callback interface
 */
internal class CardExpiryTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : AirwallexTextInputLayout(context, attrs, R.layout.card_expiry_input_layout) {

    /**
     * Callback of complete input expiry date
     */
    internal var completionCallback: () -> Unit = {}
        set(value) {
            (teInput as CardExpiryEditText).completionCallback = value
            field = value
        }

    /**
     * Check if expiry date is valid
     */
    internal val isValid: Boolean
        get() = (teInput as CardExpiryEditText).isDateValid

    /**
     * Return the valid date fields, include month & year, object of [Pair]
     */
    internal val validDateFields: Pair<Int, Int>?
        get() = (teInput as CardExpiryEditText).validDateFields

    init {
        val input = teInput as CardExpiryEditText
        input.errorCallback = { showError ->
            error = if (showError) {
                resources.getString(R.string.airwallex_invalid_expiry_date)
            } else {
                null
            }
        }
    }
}
