package com.airwallex.paymentacceptance.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.view.PaymentMethodsActivityStarter
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Store
import kotlinx.android.synthetic.main.payment_method_item.view.*

class PaymentMethodItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private var paymentMethod: PaymentMethod? = null

    internal lateinit var paymentIntent: PaymentIntent

    internal val isValid: Boolean
        get() {
            return paymentMethod != null
        }

    var cvcChangedCallback: () -> Unit = {}

    init {
        View.inflate(getContext(), R.layout.payment_method_item, this)

        llPaymentMethod.setOnClickListener {
            val customerId = paymentIntent.customerId
            if (customerId != null) {
                PaymentMethodsActivityStarter(context as Activity)
                    .startForResult(
                        PaymentMethodsActivityStarter.PaymentMethodsArgs
                            .Builder(paymentIntent.clientSecret, Store.token, customerId)
                            .setPaymentMethod(paymentMethod)
                            .setAvailablePaymentMethodTypes(paymentIntent.availablePaymentMethodTypes)
                            .build()
                    )
            } else {
                // Need merchant's own select payment method screen
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun renewalPaymentMethod(paymentMethod: PaymentMethod?) {
        this.paymentMethod = paymentMethod
        if (paymentMethod == null) {
            tvPaymentMethod.text = context.getString(R.string.select_payment_method)
            tvPaymentMethod.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.airwallex_color_dark_light
                )
            )
            return
        }

        if (paymentMethod.type == PaymentMethodType.WECHAT) {
            tvPaymentMethod.text = paymentMethod.type?.displayName
        } else {
            tvPaymentMethod.text =
                String.format(
                    "%s •••• %s",
                    paymentMethod.card?.brand?.capitalize(),
                    paymentMethod.card?.last4
                )
        }

        tvPaymentMethod.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_color_dark_deep
            )
        )
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        completion: (paymentMethod: PaymentMethod?) -> Unit
    ) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            PaymentMethodsActivityStarter.REQUEST_CODE -> {
                val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                renewalPaymentMethod(result?.paymentMethod)
                completion(paymentMethod)
            }
        }
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