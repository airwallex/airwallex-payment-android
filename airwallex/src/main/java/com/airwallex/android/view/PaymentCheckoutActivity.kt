package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexSession
import com.airwallex.android.CurrencyUtils.formatPrice
import com.airwallex.android.R
import com.airwallex.android.databinding.ActivityPaymentCheckoutBinding
import com.airwallex.android.model.PaymentConsent
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.WeChat
import java.lang.Exception

/**
 * Activity to confirm payment intent
 */
internal class PaymentCheckoutActivity : AirwallexCheckoutBaseActivity() {

    private val viewBinding: ActivityPaymentCheckoutBinding by lazy {
        viewStub.layoutResource = R.layout.activity_payment_checkout
        val root = viewStub.inflate() as ViewGroup
        ActivityPaymentCheckoutBinding.bind(root)
    }

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private val args: PaymentCheckoutActivityLaunch.Args by lazy {
        PaymentCheckoutActivityLaunch.Args.getExtra(intent)
    }

    private val paymentConsent: PaymentConsent by lazy {
        args.paymentConsent
    }

    private val session: AirwallexSession by lazy {
        args.session
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.tvTotalPrice.text = formatPrice(session.currency, session.amount)
        viewBinding.paymentMethodItemView.renewalPaymentMethod(requireNotNull(paymentConsent.paymentMethod), args.cvc)
        viewBinding.paymentMethodItemView.cvcChangedCallback = {
            updateButtonStatus()
        }

        viewBinding.rlPayNow.setOnClickListener {
            startConfirmPaymentIntent()
        }
        updateButtonStatus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        airwallex.handlePaymentData(requestCode, resultCode, data)
    }

    private fun startConfirmPaymentIntent() {
        startCheckout(
            session = session,
            paymentConsent = paymentConsent,
            cvc = viewBinding.paymentMethodItemView.cvc,
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
        viewBinding.rlPayNow.isEnabled = viewBinding.paymentMethodItemView.isValid
    }
}
