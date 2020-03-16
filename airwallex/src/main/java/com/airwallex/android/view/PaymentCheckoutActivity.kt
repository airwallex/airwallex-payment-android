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

/**
 * Activity to confirm payment intent
 */
internal class PaymentCheckoutActivity : AirwallexCheckoutBaseActivity() {

    override val airwallex: Airwallex by lazy {
        Airwallex(
            requireNotNull(args.token),
            requireNotNull(paymentIntent.clientSecret)
        )
    }

    private val args: PaymentCheckoutActivityStarter.Args by lazy {
        PaymentCheckoutActivityStarter.Args.getExtra(intent)
    }

    private val paymentMethod: PaymentMethod by lazy {
        args.paymentMethod
    }

    override val paymentIntent: PaymentIntent by lazy {
        args.paymentIntent
    }

    override val requestThreeDSecure: Boolean by lazy {
        args.requestThreeDSecure
    }
    override val cvc: String?
        get() = paymentMethodItemView.cvc

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            startConfirmPaymentIntent()
        }
        updateButtonStatus()
    }

    override val layoutResource: Int
        get() = R.layout.activity_payment_checkout

    override fun onBackPressed() {
        if (loadingView.visibility == View.GONE) {
            super.onBackPressed()
        }
    }

    private fun startConfirmPaymentIntent() {
        confirmPaymentIntent(paymentMethod = paymentMethod,
            callback = object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onSuccess(response: PaymentIntent) {
                    finishWithPaymentIntent(paymentIntent = response, type = paymentMethod.type)
                }

                override fun onFailed(exception: AirwallexException) {
                    finishWithPaymentIntent(error = exception.error)
                }
            })
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
