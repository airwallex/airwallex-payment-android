package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.airwallex.android.Airwallex
import com.airwallex.android.model.ContinuePaymentIntentParams
import com.airwallex.android.Logger
import com.airwallex.android.R
import com.airwallex.android.databinding.ActivitySelectCurrencyBinding
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentIntentContinueType

/**
 * Allow the customer to select your currency.
 */
internal class DccActivity : AirwallexActivity() {

    private val viewBinding: ActivitySelectCurrencyBinding by lazy {
        viewStub.layoutResource = R.layout.activity_select_currency
        val root = viewStub.inflate() as ViewGroup
        ActivitySelectCurrencyBinding.bind(root)
    }

    private val airwallex: Airwallex by lazy { Airwallex(this) }
    private val args: DccActivityLaunch.Args by lazy { DccActivityLaunch.Args.getExtra(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Current currency
        viewBinding.currentCurrency.updateCurrency(args.currency, args.amount)

        // Transfer currency
        val dccCurrency = args.dcc.currency
        val dccAmount = args.dcc.amount
        if (dccCurrency != null && dccAmount != null) {
            viewBinding.transferCurrency.updateCurrency(dccCurrency, dccAmount)
        } else {
            viewBinding.transferCurrency.visibility = View.GONE
        }

        viewBinding.rate.text = getString(R.string.rate, args.currency, args.dcc.clientRate, args.dcc.currency)
        viewBinding.currentCurrency.isSelected = true
        viewBinding.currentCurrency.setOnClickListener {
            Logger.debug("Current currency selected")
            viewBinding.currentCurrency.isSelected = true
            viewBinding.transferCurrency.isSelected = false
        }
        viewBinding.transferCurrency.setOnClickListener {
            Logger.debug("Transfer currency selected")
            viewBinding.currentCurrency.isSelected = false
            viewBinding.transferCurrency.isSelected = true
        }

        viewBinding.confirm.setOnClickListener {
            setLoadingProgress(loading = true, cancelable = false)
            val params = ContinuePaymentIntentParams(
                paymentIntentId = args.paymentIntentId,
                clientSecret = args.clientSecret,
                type = PaymentIntentContinueType.DCC,
                useDcc = viewBinding.transferCurrency.isSelected
            )
            airwallex.continuePaymentIntent(
                applicationContext, ThreeDSecureActivityLaunch(this), params,
                object : Airwallex.PaymentListener<PaymentIntent> {
                    override fun onFailed(exception: Exception) {
                        finishWithPaymentIntent(exception = exception)
                    }

                    override fun onSuccess(response: PaymentIntent) {
                        finishWithPaymentIntent(paymentIntent = response)
                    }
                }
            )
        }
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        exception: Exception? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                DccActivityLaunch.Result(
                    paymentIntent = paymentIntent,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    override fun onActionSave() {
        // Ignore
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        airwallex.handlePaymentData(requestCode, resultCode, data)
    }
}
