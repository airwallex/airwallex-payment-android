package com.airwallex.paymentacceptance

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethod.Card.Companion.CVC_LENGTH
import com.airwallex.paymentacceptance.view.ErrorListener
import kotlinx.android.synthetic.main.widget_card.view.*

class CardWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),
    TextWatcher {

    internal var completionCallback: () -> Unit = {}

    internal var cardChangeCallback: () -> Unit = {}

    val paymentMethodCard: PaymentMethod.Card?
        get() {
            return if (isValidCard) {
                etExpires.validDateFields?.let { (month, year) ->
                    PaymentMethod.Card.Builder()
                        .setNumber(etCardNumber.cardNumber)
                        .setExpMonth(month.toString())
                        .setExpYear(year.toString())
                        .setCvc(etCvc.text.toString())
                        .build()
                }
            } else {
                null
            }
        }

    val isValidCard: Boolean
        get() {
            val cardNumberIsValid = CardUtils.isValidCardNumber(etCardNumber.cardNumber)
            val expiryIsValid = etExpires.validDateFields != null
            val cvcIsValid = etCvc.text.toString().trim().length == CVC_LENGTH
            return cardNumberIsValid && expiryIsValid && cvcIsValid
        }

    init {
        View.inflate(getContext(), R.layout.widget_card, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etCardNumber.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER)
            etExpires.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE)
        }

        etCardNumber.setErrorMessageListener(ErrorListener(tlCardNumber))
        etExpires.setErrorMessageListener(ErrorListener(tlExpires))
        etCvc.setErrorMessageListener(ErrorListener(tlCvc))

        etCardNumber.setErrorMessage(context.getString(R.string.invalid_card_number))
        etExpires.setErrorMessage(context.getString(R.string.invalid_expiry_year))
        etCvc.setErrorMessage(context.getString(R.string.invalid_cvc))

        etCardNumber.addTextChangedListener(this)
        etCardName.addTextChangedListener(this)
        etExpires.addTextChangedListener(this)
        etCvc.addTextChangedListener(this)

        etCardNumber.completionCallback = {
            etCardName.requestFocus()
        }

        etExpires.completionCallback = {
            etCvc.requestFocus()
        }

        etCvc.completionCallback = {
            completionCallback.invoke()
        }
    }

    override fun afterTextChanged(s: Editable?) {
        cardChangeCallback.invoke()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}