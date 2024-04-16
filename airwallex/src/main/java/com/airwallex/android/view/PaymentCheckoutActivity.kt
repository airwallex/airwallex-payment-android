package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.WeChat
import com.airwallex.android.core.util.CurrencyUtils.formatPrice
import com.airwallex.android.databinding.ActivityPaymentCheckoutBinding
import com.airwallex.android.R
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.log.AnalyticsLogger
import java.util.*

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

    override fun onBackButtonPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.header.text = getString(
            R.string.airwallex_enter_cvv,
            String.format(
                "%s •••• %s",
                paymentMethod.card?.brand?.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale.getDefault()
                        )
                    } else it.toString()
                },
                paymentMethod.card?.last4
            )
        )
        viewBinding.tvTotalPrice.text =
            getString(R.string.airwallex_total, formatPrice(session.currency, session.amount))

        viewBinding.atlCardCvc.afterTextChanged {
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
            cvc = viewBinding.atlCardCvc.value,
            observer = { result ->
                when (result) {
                    is AirwallexPaymentStatus.Success -> {
                        AnalyticsLogger.logAction(
                            "payment_success",
                            mutableMapOf<String, String>().apply {
                                putIfNotNull("paymentMethod", paymentMethod.type)
                            }
                        )

                        finishWithPaymentIntent(paymentIntentId = result.paymentIntentId)
                    }
                    is AirwallexPaymentStatus.Failure -> {
                        finishWithPaymentIntent(exception = result.exception)
                    }
                    else -> Unit
                }
            }
        )
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        weChat: WeChat? = null,
        redirectUrl: String? = null,
        exception: AirwallexException? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentCheckoutActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    weChat = weChat,
                    redirectUrl = redirectUrl,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    private fun updateButtonStatus() {
        viewBinding.rlPayNow.isEnabled = viewBinding.atlCardCvc.isValid
    }
}
