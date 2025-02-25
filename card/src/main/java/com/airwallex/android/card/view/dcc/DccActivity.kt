package com.airwallex.android.card.view.dcc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.card.R
import com.airwallex.android.card.databinding.ActivitySelectCurrencyBinding
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.ContinuePaymentIntentParams
import com.airwallex.android.core.model.PaymentIntentContinueType
import com.airwallex.android.ui.AirwallexActivity
import com.airwallex.android.ui.extension.getExtraArgs

/**
 * Allow the customer to select your currency.
 */
class DccActivity : AirwallexActivity() {

    private val viewBinding: ActivitySelectCurrencyBinding by lazy {
        viewStub.layoutResource = R.layout.activity_select_currency
        val root = viewStub.inflate() as ViewGroup
        ActivitySelectCurrencyBinding.bind(root)
    }

    private val airwallex: Airwallex by lazy { Airwallex(this) }
    private val args: DccActivityLaunch.Args by lazy { intent.getExtraArgs() }
    private val viewModel: DccViewModel by lazy {
        ViewModelProvider(
            this,
            DccViewModel.Factory(
                application,
                airwallex
            )
        )[DccViewModel::class.java]
    }

    override fun onBackButtonPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

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

        viewBinding.rate.text =
            getString(
                R.string.airwallex_rate,
                args.currency,
                args.dcc.clientRate,
                args.dcc.currency
            )
        viewBinding.currentCurrency.isSelected = true
        viewBinding.currentCurrency.setOnSingleClickListener {
            AirwallexLogger.debug("Current currency selected")
            viewBinding.currentCurrency.isSelected = true
            viewBinding.transferCurrency.isSelected = false
        }
        viewBinding.transferCurrency.setOnSingleClickListener {
            AirwallexLogger.debug("Transfer currency selected")
            viewBinding.currentCurrency.isSelected = false
            viewBinding.transferCurrency.isSelected = true
        }

        viewBinding.confirm.setOnSingleClickListener {
            setLoadingProgress(loading = true, cancelable = false)
            val params = ContinuePaymentIntentParams(
                paymentIntentId = args.paymentIntentId,
                clientSecret = args.clientSecret,
                type = PaymentIntentContinueType.DCC,
                useDcc = viewBinding.transferCurrency.isSelected
            )
            viewModel.continuePaymentIntent(params)
                .observe(
                    this,
                    { result ->
                        result.fold(
                            onSuccess = {
                                finishWithPaymentIntent(paymentIntentId = it)
                            },
                            onFailure = {
                                finishWithPaymentIntent(exception = it as AirwallexException)
                            }
                        )
                    }
                )
        }
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        exception: AirwallexException? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                DccActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }
}
