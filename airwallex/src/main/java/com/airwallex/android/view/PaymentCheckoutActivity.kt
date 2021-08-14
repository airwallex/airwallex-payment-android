package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.WeChat
import com.airwallex.android.core.util.CurrencyUtils.formatPrice
import com.airwallex.android.databinding.ActivityPaymentCheckoutBinding
import com.airwallex.android.R

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

    private val paymentMethod: PaymentMethod by lazy {
        args.paymentMethod
    }

    private val paymentConsentId: String? by lazy {
        args.paymentConsentId
    }

    override val session: AirwallexSession by lazy {
        args.session
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.tvTotalPrice.text = formatPrice(session.currency, session.amount)
        viewBinding.paymentMethodItemView.renewalPaymentMethod(paymentMethod, args.cvc)
        viewBinding.paymentMethodItemView.cvcChangedCallback = {
            updateButtonStatus()
        }

        viewBinding.rlPayNow.setOnSingleClickListener {
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
            paymentMethod = paymentMethod,
            paymentConsentId = paymentConsentId,
            cvc = viewBinding.paymentMethodItemView.cvc,
            observer = {
                when (it) {
                    is AirwallexCheckoutViewModel.PaymentResult.Success -> {
                        finishWithPaymentIntent(paymentIntent = it.paymentIntent)
                    }
                    is AirwallexCheckoutViewModel.PaymentResult.Error -> {
                        finishWithPaymentIntent(exception = it.exception)
                    }
                    is AirwallexCheckoutViewModel.PaymentResult.WeChatPay -> {
                        finishWithPaymentIntent(weChat = it.weChat)
                    }
                    is AirwallexCheckoutViewModel.PaymentResult.Redirect -> {
                        finishWithPaymentIntent(redirectUrl = it.redirectUrl)
                    }
                }
            }
        )
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        weChat: WeChat? = null,
        redirectUrl: String? = null,
        exception: AirwallexException? = null
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
