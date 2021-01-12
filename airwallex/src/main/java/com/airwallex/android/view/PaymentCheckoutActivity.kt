package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.Airwallex
import com.airwallex.android.CurrencyUtils.formatPrice
import com.airwallex.android.R
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.synthetic.main.activity_payment_checkout.*
import java.lang.Exception

/**
 * Activity to confirm payment intent
 */
internal class PaymentCheckoutActivity : AirwallexCheckoutBaseActivity() {

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private val args: PaymentCheckoutActivityLaunch.Args by lazy {
        PaymentCheckoutActivityLaunch.Args.getExtra(intent)
    }

    private val paymentMethod: PaymentMethod by lazy {
        args.paymentMethod
    }

    override val paymentIntent: PaymentIntent by lazy {
        args.paymentIntent
    }

    override val cvc: String?
        get() = paymentMethodItemView.cvc

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvTotalPrice.text = formatPrice(paymentIntent.currency, paymentIntent.amount)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        airwallex.handlePaymentData(requestCode, resultCode, data)
    }

    private fun startConfirmPaymentIntent() {
        confirmPaymentIntent(
            paymentMethod = paymentMethod,
            listener = object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onSuccess(response: PaymentIntent) {
                    finishWithPaymentIntent(paymentIntent = response)
                }

                override fun onFailed(exception: Exception) {
                    finishWithPaymentIntent(exception = exception)
                }
            }
        )
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        exception: Exception? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentCheckoutActivityLaunch.Result(
                    paymentIntent = paymentIntent,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    private fun updateButtonStatus() {
        rlPayNow.isEnabled = paymentMethodItemView.isValid
    }
}
