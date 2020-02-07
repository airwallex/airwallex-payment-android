package com.airwallex.paymentacceptance

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.synthetic.main.widget_card.view.*

class CardWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    internal var completionCallback: () -> Unit = {}

    internal var cardChangeCallback: () -> Unit = {}

    val paymentMethodCard: PaymentMethod.Card?
        get() {
            return if (isValidCard) {
                atlCardExpiry.validDateFields?.let { (month, year) ->
                    PaymentMethod.Card.Builder()
                        .setNumber(atlCardNumber.cardNumber)
                        .setName(atlCardName.value)
                        .setExpMonth(month.toString())
                        .setExpYear(year.toString())
                        .setCvc(atlCardCvc.cvcValue)
                        .build()
                }
            } else {
                null
            }
        }

    val isValidCard: Boolean
        get() {
            val cardNumberIsValid = CardUtils.isValidCardNumber(atlCardNumber.cardNumber)
            val expiryIsValid = atlCardExpiry.validDateFields != null
            val cvcIsValid = atlCardCvc.isValid
            return cardNumberIsValid && expiryIsValid && cvcIsValid
        }

    init {
        View.inflate(getContext(), R.layout.widget_card, this)

        listenTextChanged()
        listenFocusChanged()
        listenCompletionCallback()
    }

    private fun listenTextChanged() {
        atlCardNumber.afterTextChanged { cardChangeCallback.invoke() }
        atlCardName.afterTextChanged { cardChangeCallback.invoke() }
        atlCardExpiry.afterTextChanged { cardChangeCallback.invoke() }
        atlCardCvc.afterTextChanged { cardChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
        atlCardNumber.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    atlCardNumber.value.isEmpty() -> {
                        atlCardNumber.error = resources.getString(R.string.empty_card_number)
                    }
                    !atlCardNumber.isValid -> {
                        atlCardNumber.error = resources.getString(R.string.invalid_card_number)
                    }
                    else -> {
                        atlCardNumber.error = null
                    }
                }
            } else {
                atlCardNumber.error = null
            }
        }
        atlCardName.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    atlCardName.value.isEmpty() -> {
                        atlCardName.error = resources.getString(R.string.empty_card_name)
                    }
                    else -> {
                        atlCardName.error = null
                    }
                }
            } else {
                atlCardName.error = null
            }
        }
        atlCardExpiry.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    atlCardExpiry.value.isEmpty() -> {
                        atlCardExpiry.error = resources.getString(R.string.empty_expiry)
                    }
                    !atlCardExpiry.isValid -> {
                        atlCardExpiry.error = resources.getString(R.string.invalid_expiry)
                    }
                    else -> {
                        atlCardExpiry.error = null
                    }
                }
            } else {
                atlCardExpiry.error = null
            }
        }
        atlCardCvc.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    atlCardCvc.value.isEmpty() -> {
                        atlCardCvc.error = resources.getString(R.string.empty_cvc)
                    }
                    !atlCardCvc.isValid -> {
                        atlCardCvc.error = resources.getString(R.string.invalid_cvc)
                    }
                    else -> {
                        atlCardCvc.error = null
                    }
                }
            } else {
                atlCardCvc.error = null
            }
        }
    }

    private fun listenCompletionCallback() {
        atlCardNumber.completionCallback = { atlCardName.requestInputFocus() }
        atlCardExpiry.completionCallback = { atlCardCvc.requestInputFocus() }
        atlCardCvc.completionCallback = { completionCallback.invoke() }
    }
}