package com.airwallex.android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.ui.R
import com.airwallex.android.ui.databinding.WidgetCardBinding

/**
 * A widget used to collect the card info
 */
class CardWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val viewBinding = WidgetCardBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val cardNameTextInputLayout = viewBinding.atlCardName
    private val cardNumberTextInputLayout = viewBinding.atlCardNumber
    private val cvcTextInputLayout = viewBinding.atlCardCvc
    private val expiryTextInputLayout = viewBinding.atlCardExpiry

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
            val cardNumberIsValid = CardUtils.isValidCardNumber(cardNumberTextInputLayout.cardNumber)
            val cardNameIsValid = cardNameTextInputLayout.value.isNotEmpty()
            val expiryIsValid = expiryTextInputLayout.validDateFields != null
            val cvcIsValid = cvcTextInputLayout.isValid
            return cardNumberIsValid && cardNameIsValid && expiryIsValid && cvcIsValid
        }

    init {
        listenTextChanged()
        listenFocusChanged()
        listenCompletionCallback()
    }

    private fun listenTextChanged() {
        cardNumberTextInputLayout.afterTextChanged { cardChangeCallback.invoke() }
        cardNameTextInputLayout.afterTextChanged { cardChangeCallback.invoke() }
        expiryTextInputLayout.afterTextChanged { cardChangeCallback.invoke() }
        cvcTextInputLayout.afterTextChanged { cardChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
        cardNumberTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    cardNumberTextInputLayout.value.isEmpty() -> {
                        cardNumberTextInputLayout.error = resources.getString(R.string.airwallex_empty_card_number)
                    }
                    !cardNumberTextInputLayout.isValid -> {
                        cardNumberTextInputLayout.error = resources.getString(R.string.airwallex_invalid_card_number)
                    }
                    else -> {
                        cardNumberTextInputLayout.error = null
                    }
                }
            } else {
                cardNumberTextInputLayout.error = null
            }
        }
        cardNameTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    cardNameTextInputLayout.value.isEmpty() -> {
                        cardNameTextInputLayout.error = resources.getString(R.string.airwallex_empty_card_name)
                    }
                    else -> {
                        cardNameTextInputLayout.error = null
                    }
                }
            } else {
                cardNameTextInputLayout.error = null
            }
        }
        expiryTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    expiryTextInputLayout.value.isEmpty() -> {
                        expiryTextInputLayout.error = resources.getString(R.string.airwallex_empty_expiry)
                    }
                    !expiryTextInputLayout.isValid -> {
                        expiryTextInputLayout.error = resources.getString(R.string.airwallex_invalid_expiry_date)
                    }
                    else -> {
                        expiryTextInputLayout.error = null
                    }
                }
            } else {
                expiryTextInputLayout.error = null
            }
        }
        cvcTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    cvcTextInputLayout.value.isEmpty() -> {
                        cvcTextInputLayout.error = resources.getString(R.string.airwallex_empty_cvc)
                    }
                    !cvcTextInputLayout.isValid -> {
                        cvcTextInputLayout.error = resources.getString(R.string.airwallex_invalid_cvc)
                    }
                    else -> {
                        cvcTextInputLayout.error = null
                    }
                }
            } else {
                cvcTextInputLayout.error = null
            }
        }
    }

    private fun listenCompletionCallback() {
        cardNumberTextInputLayout.completionCallback = { cardNameTextInputLayout.requestInputFocus() }
        expiryTextInputLayout.completionCallback = { cvcTextInputLayout.requestInputFocus() }
    }
}
