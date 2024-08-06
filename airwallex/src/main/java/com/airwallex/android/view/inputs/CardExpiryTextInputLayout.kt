package com.airwallex.android.view.inputs

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R
import com.airwallex.android.ui.widget.AirwallexTextInputLayout
import com.airwallex.android.ui.widget.ValidatedInput

/**
 * A [AirwallexTextInputLayout] to format the credit card expiry date, display errors and support callback interface
 */
internal class CardExpiryTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet?
) : AirwallexTextInputLayout(context, attrs, R.layout.card_expiry_input_layout), ValidatedInput {

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
    override val isValid: Boolean
        get() = (teInput as CardExpiryEditText).isDateValid

    override val emptyErrorMessage: String
        get() = resources.getString(R.string.airwallex_empty_expiry)

    override val invalidErrorMessage: String
        get() = resources.getString(R.string.airwallex_invalid_expiry_date)

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
