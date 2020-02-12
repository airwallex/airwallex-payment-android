package com.airwallex.paymentacceptance.view

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
import com.airwallex.paymentacceptance.PaymentBaseActivity
import com.airwallex.paymentacceptance.PaymentMethodsActivity
import com.airwallex.paymentacceptance.R
import kotlinx.android.synthetic.main.payment_method_item.view.*

class PaymentMethodItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private var paymentMethod: PaymentMethod? = null

    internal lateinit var paymentIntent: PaymentIntent

    internal val cvc: String?
        get() {
            return if (isValid) {
                atlCardCvc.value
            } else {
                null
            }
        }

    internal val isValid: Boolean
        get() {
            return atlCardCvc.isValid && paymentMethod != null
        }

    var cvcChangedCallback: () -> Unit = {}


    init {
        View.inflate(getContext(), R.layout.payment_method_item, this)

        llPaymentMethod.setOnClickListener {
            PaymentMethodsActivity.startActivityForResult(
                context as Activity,
                paymentMethod,
                paymentIntent,
                PaymentBaseActivity.REQUEST_PAYMENT_METHOD_CODE
            )
        }

        atlCardCvc.changedCallback = {
            if (atlCardCvc.isValid) {
                hideKeyboard(context as Activity)
            }
            cvcChangedCallback()
        }
    }

    fun renewalPaymentMethod(paymentMethod: PaymentMethod?) {
        this.paymentMethod = paymentMethod
        if (paymentMethod == null) {
            tvPaymentMethod.text = context.getString(R.string.select_payment_method)
            tvPaymentMethod.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.airwallex_dark_light
                )
            )
            llCardCvc.visibility = View.GONE
            return
        }

        if (paymentMethod.type == PaymentMethodType.WECHAT) {
            tvPaymentMethod.text = paymentMethod.type?.displayName
            llCardCvc.visibility = View.GONE
        } else {
            tvPaymentMethod.text =
                String.format("%s •••• %s", paymentMethod.card?.brand, paymentMethod.card?.last4)
            llCardCvc.visibility = View.VISIBLE
        }

        tvPaymentMethod.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_dark_gray
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
            PaymentBaseActivity.REQUEST_PAYMENT_METHOD_CODE -> {
                paymentMethod =
                    data.getParcelableExtra(PaymentBaseActivity.PAYMENT_METHOD) as? PaymentMethod
                renewalPaymentMethod(paymentMethod)
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
        if (inputMethodManager?.isAcceptingText == true) {
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }
    }
}