package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.databinding.WidgetCardBinding

/**
 * A widget used to collect the card info
 */
class CardWidget(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val viewBinding = WidgetCardBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val cardNameTextInputLayout = viewBinding.atlCardName
    private val cardNumberTextInputLayout = viewBinding.atlCardNumber
    private val cvcTextInputLayout = viewBinding.atlCardCvc
    private val expiryTextInputLayout = viewBinding.atlCardExpiry
    private val validatedTextInputs =
        listOf<ValidatedInput>(cardNameTextInputLayout, cvcTextInputLayout, expiryTextInputLayout)

    var validationMessageCallback: (String) -> String? = { null }
        set(value) {
            cardNumberTextInputLayout.validationMessageCallback = value
            field = value
        }

    var cardChangeCallback: () -> Unit = {}

    val paymentMethodCard: PaymentMethod.Card?
        get() {
            return if (isValid) {
                expiryTextInputLayout.validDateFields?.let { (month, year) ->
                    PaymentMethod.Card.Builder()
                        .setNumber(cardNumberTextInputLayout.cardNumber)
                        .setName(cardNameTextInputLayout.value)
                        .setExpiryMonth(if (month < 10) "0$month" else month.toString())
                        .setExpiryYear(year.toString())
                        .setCvc(cvcTextInputLayout.cvcValue)
                        .build()
                }
            } else {
                null
            }
        }

    /**
     * Check the card info to see whether or not it is a valid card
     */
    val isValid: Boolean
        get() {
            return cardNumberTextInputLayout.isValid && validatedTextInputs.all { it.isValid }
        }

    init {
        listenTextChanged()
        listenFocusChanged()
        listenCompletionCallback()
    }

    private fun listenTextChanged() {
        cardNumberTextInputLayout.afterTextChanged { cardChangeCallback() }
        validatedTextInputs.forEach { input ->
            if (input is AirwallexTextInputLayout) {
                input.afterTextChanged { cardChangeCallback() }
            }
        }

    }

    private fun listenFocusChanged() {
        for (input in validatedTextInputs) {
            if (input is AirwallexTextInputLayout) {
                input.afterFocusChanged { hasFocus ->
                    input.error = if (!hasFocus) {
                        when {
                            input.value.isEmpty() -> input.emptyErrorMessage
                            !input.isValid -> input.invalidErrorMessage
                            else -> null
                        }
                    } else {
                        null
                    }
                }
            }
        }
    }

    private fun listenCompletionCallback() {
        cardNumberTextInputLayout.completionCallback =
            { cardNameTextInputLayout.requestInputFocus() }
        expiryTextInputLayout.completionCallback = { cvcTextInputLayout.requestInputFocus() }
    }
}
