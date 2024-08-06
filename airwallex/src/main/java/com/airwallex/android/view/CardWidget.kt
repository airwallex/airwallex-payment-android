package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.databinding.WidgetCardBinding
import com.airwallex.android.ui.widget.AirwallexTextInputLayout
import com.airwallex.android.ui.widget.ValidatedInput
import com.airwallex.risk.AirwallexRisk

/**
 * A widget used to collect the card info
 */
open class CardWidget(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val viewBinding = WidgetCardBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val cardNameTextInputLayout = viewBinding.atlCardName
    private val cardNumberTextInputLayout = viewBinding.atlCardNumber
    private val cvcTextInputLayout = viewBinding.atlCardCvc
    private val expiryTextInputLayout = viewBinding.atlCardExpiry
    private val emailTextInputLayout = viewBinding.atlCardEmail
    private val validatedTextInputs: List<ValidatedInput>
        get() {
            return mutableListOf<ValidatedInput>(cardNameTextInputLayout, cvcTextInputLayout, expiryTextInputLayout)
                .apply {
                    if (showEmail) {
                        add(emailTextInputLayout)
                    }
                }
        }

    var validationMessageCallback: (String) -> String? = { null }
        set(value) {
            cardNumberTextInputLayout.validationMessageCallback = value
            field = value
        }

    var brandChangeCallback: (CardBrand) -> Unit = {}
        set(value) {
            cardNumberTextInputLayout.brandChangeCallback = value
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

    /**
     * Decide if the email input field is shown
     */
    var showEmail = false
        set(value) {
            if (value) {
                emailTextInputLayout.visibility = VISIBLE
                emailTextInputLayout.afterTextChanged { cardChangeCallback() }
                emailTextInputLayout.listenFocusChanged()
                cvcTextInputLayout.setImeOptions(EditorInfo.IME_ACTION_NEXT)
            } else {
                emailTextInputLayout.visibility = GONE
                cvcTextInputLayout.setImeOptions(EditorInfo.IME_ACTION_DONE)
            }
            field = value
        }

    init {
        listenTextChanged()
        listenFocusChanged()
        listenCompletionCallback()
        listenClick()
    }

    private fun listenClick() {
        cardNumberTextInputLayout.setOnInputEditTextClickListener {
            AirwallexRisk.log(event = "input_card_number", screen = "page_create_card")
        }
        cardNameTextInputLayout.setOnInputEditTextClickListener {
            AirwallexRisk.log(event = "input_card_holder_name", screen = "page_create_card")
        }
        expiryTextInputLayout.setOnInputEditTextClickListener {
            AirwallexRisk.log(event = "input_card_expiry", screen = "page_create_card")
        }
        cvcTextInputLayout.setOnInputEditTextClickListener {
            AirwallexRisk.log(event = "input_card_cvc", screen = "page_create_card")
        }
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
        validatedTextInputs.forEach { it.listenFocusChanged() }
    }

    private fun listenCompletionCallback() {
        cardNumberTextInputLayout.completionCallback = { cardNameTextInputLayout.requestInputFocus() }
        expiryTextInputLayout.completionCallback = { cvcTextInputLayout.requestInputFocus() }
    }

    private fun ValidatedInput.listenFocusChanged() {
        if (this is AirwallexTextInputLayout) {
            afterFocusChanged { hasFocus ->
                error = if (!hasFocus) {
                    when {
                        value.isEmpty() -> emptyErrorMessage
                        !isValid -> invalidErrorMessage
                        else -> null
                    }
                } else {
                    null
                }
            }
        }
    }
}
