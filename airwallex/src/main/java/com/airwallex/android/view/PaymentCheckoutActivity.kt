package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import java.util.*
import kotlinx.android.synthetic.main.activity_airwallex.*
import kotlinx.android.synthetic.main.activity_payment_checkout.*

internal class PaymentCheckoutActivity : AirwallexActivity() {

    private val airwallex: Airwallex by lazy {
        Airwallex(
            args.token,
            paymentIntent.clientSecret!!
        )
    }

    private val paymentMethod: PaymentMethod
        get() {
            return args.paymentMethod
        }

    private val paymentIntent: PaymentIntent
        get() {
            return args.paymentIntent
        }

    private val args: PaymentCheckoutActivityStarter.Args by lazy {
        PaymentCheckoutActivityStarter.Args.getExtra(intent)
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_payment_checkout
        viewStub.inflate()

        tvTotalPrice.text = String.format(
            "%s%.2f",
            Currency.getInstance(paymentIntent.currency).symbol,
            paymentIntent.amount
        )
        paymentMethodItemView.renewalPaymentMethod(paymentMethod, args.cvc)
        paymentMethodItemView.cvcChangedCallback = {
            updateButtonStatus()
        }

        rlPayNow.setOnClickListener {
            onActionSave()
        }
        updateButtonStatus()
    }

    override fun onBackPressed() {
        if (loadingView.visibility == View.GONE) {
            super.onBackPressed()
        }
    }

    override fun onActionSave() {
        setLoadingProgress(true)
        airwallex.confirmPaymentIntent(
            paymentIntentId = paymentIntent.id,
            paymentIntentParams = buildPaymentIntentParams(paymentMethod, paymentIntent.customerId),
            callback = object : Airwallex.PaymentCallback<PaymentIntent> {
                override fun onSuccess(response: PaymentIntent) {
                    finishWithPaymentIntent(paymentIntent = response, type = paymentMethod.type)
                }

                override fun onFailed(exception: AirwallexException) {
                    finishWithPaymentIntent(error = exception.error)
                }
            }
        )
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        type: PaymentMethodType? = null,
        error: AirwallexError? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK, Intent().putExtras(
                    PaymentCheckoutActivityStarter.Result(
                        paymentIntent = paymentIntent,
                        paymentMethodType = type,
                        error = error
                    ).toBundle()
                )
        )
        finish()
    }

    private fun updateButtonStatus() {
        rlPayNow.isEnabled = paymentMethodItemView.isValid
    }
}
