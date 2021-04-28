package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexSession
import com.airwallex.android.CurrencyUtils.formatPrice
import com.airwallex.android.R
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.WeChat
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

    private val session: AirwallexSession by lazy {
        args.session
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvTotalPrice.text = formatPrice(session.currency, session.amount)
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
        startCheckout(
            session = session,
            paymentMethod = paymentMethod,
            cvc = paymentMethodItemView.cvc,
            listener = object : Airwallex.PaymentResultListener<PaymentIntent> {
                override fun onSuccess(response: PaymentIntent) {
                    finishWithPaymentIntent(paymentIntent = response)
                }

                override fun onFailed(exception: Exception) {
                    finishWithPaymentIntent(exception = exception)
                }

                override fun onNextActionWithWeChatPay(weChat: WeChat) {
                    finishWithPaymentIntent(weChat = weChat)
                }

                override fun onNextActionWithAlipayUrl(url: String) {
                    finishWithPaymentIntent(redirectUrl = url)
                }
            }
        )
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        weChat: WeChat? = null,
        redirectUrl: String? = null,
        exception: Exception? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentCheckoutActivityLaunch.Result(
                    paymentIntent = paymentIntent,
                    weChat = weChat,
                    redirectUrl = redirectUrl,
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
