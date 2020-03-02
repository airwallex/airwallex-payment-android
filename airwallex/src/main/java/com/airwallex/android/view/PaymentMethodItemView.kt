package com.airwallex.android.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.R
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import kotlinx.android.synthetic.main.payment_method_item.view.*

class PaymentMethodItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private lateinit var paymentMethod: PaymentMethod

    internal val isValid: Boolean
        get() {
            return etCardCvc.text?.trim().toString().length == 3
        }

    var cvcChangedCallback: () -> Unit = {}

    init {
        View.inflate(getContext(), R.layout.payment_method_item, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etCardCvc.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
        }

        etCardCvc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValid) {
                    hideKeyboard(context as Activity)
                }
                cvcChangedCallback()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    @SuppressLint("DefaultLocale")
    fun renewalPaymentMethod(paymentMethod: PaymentMethod, cvc: String?) {
        this.paymentMethod = paymentMethod
        if (paymentMethod.type == PaymentMethodType.WECHAT) {
            tvPaymentMethod.text = paymentMethod.type.displayName
            llCardCvc.visibility = View.GONE
        } else {
            tvPaymentMethod.text = String.format(
                "%s •••• %s",
                paymentMethod.card?.brand?.capitalize(),
                paymentMethod.card?.last4
            )
            llCardCvc.visibility = View.VISIBLE
            etCardCvc.setText(cvc)
        }

        tvPaymentMethod.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_color_dark_deep
            )
        )
    }

    override fun onDetachedFromWindow() {
        hideKeyboard(context as Activity)
        super.onDetachedFromWindow()
    }

    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }
}