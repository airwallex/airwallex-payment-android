package com.airwallex.android.view

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.airwallex.android.R
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import kotlinx.android.synthetic.main.payment_method_item.view.*
import java.util.*

internal class PaymentMethodItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private lateinit var paymentMethodType: PaymentMethodType

    /**
     * CVC of credit card
     */
    internal var cvc: String? = null

    internal val isValid: Boolean
        get() {
            return paymentMethodType == PaymentMethodType.CARD && cvc?.length == CardCvcEditText.VALID_CVC_LENGTH ||
                paymentMethodType == PaymentMethodType.WECHAT
        }

    /**
     * CVC changed callback
     */
    internal var cvcChangedCallback: () -> Unit = {}

    private val keyboardController: KeyboardController by lazy {
        KeyboardController(context as Activity)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cvc = etCardCvc.text?.trim().toString()
            if (isValid) {
                keyboardController.hide()
            }
            cvcChangedCallback()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    init {
        View.inflate(getContext(), R.layout.payment_method_item, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etCardCvc.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
        }

        etCardCvc.addTextChangedListener(textWatcher)
    }

    internal fun renewalPaymentMethod(paymentMethod: PaymentMethod, cvc: String?) {
        if (paymentMethod.type == null) {
            return
        }
        this.paymentMethodType = paymentMethod.type
        if (paymentMethod.type == PaymentMethodType.WECHAT) {
            tvPaymentMethod.text = paymentMethod.type.value
        } else {
            tvPaymentMethod.text = String.format(
                "%s •••• %s",
                paymentMethod.card?.brand?.toUpperCase(Locale.ROOT),
                paymentMethod.card?.last4
            )
        }

        etCardCvc.setText(cvc)

        tvPaymentMethod.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_color_dark_deep
            )
        )
    }

    override fun onDetachedFromWindow() {
        keyboardController.hide()
        super.onDetachedFromWindow()
    }
}
