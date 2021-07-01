package com.airwallex.android.view

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.R
import com.airwallex.android.databinding.PaymentMethodItemBinding
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import java.util.*

internal class PaymentMethodItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private val viewBinding = PaymentMethodItemBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var paymentMethodType: PaymentMethodType? = null

    /**
     * CVC of credit card
     */
    internal var cvc: String? = null

    internal val isValid: Boolean
        get() {
            return cvc?.length == CardCvcEditText.VALID_CVC_LENGTH && paymentMethodType == PaymentMethodType.CARD
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
            cvc = viewBinding.etCardCvc.text?.trim().toString()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            viewBinding.etCardCvc.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
        }

        viewBinding.etCardCvc.addTextChangedListener(textWatcher)
    }

    internal fun renewalPaymentMethod(paymentMethod: PaymentMethod, cvc: String?) {
        if (paymentMethod.type == null) {
            return
        }
        this.paymentMethodType = paymentMethod.type
        viewBinding.tvPaymentMethod.text = String.format(
            "%s •••• %s",
            paymentMethod.card?.brand?.uppercase(Locale.ROOT),
            paymentMethod.card?.last4
        )
        viewBinding.etCardCvc.setText(cvc)
        viewBinding.tvPaymentMethod.setTextColor(
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
