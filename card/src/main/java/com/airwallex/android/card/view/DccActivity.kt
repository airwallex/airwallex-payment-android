package com.airwallex.android.card.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.card.R
import com.airwallex.android.card.databinding.ActivitySelectCurrencyBinding
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.log.Logger
import com.airwallex.android.core.model.ContinuePaymentIntentParams
import com.airwallex.android.core.model.PaymentIntentContinueType
import com.airwallex.android.ui.AirwallexActivity

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
    private val args: DccActivityLaunch.Args by lazy { DccActivityLaunch.Args.getExtra(intent) }
    private val viewModel: DccViewModel by lazy {
        ViewModelProvider(
            this,
            DccViewModel.Factory(
                application,
                airwallex
            )
        )[DccViewModel::class.java]
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
            Logger.debug("Current currency selected")
            viewBinding.currentCurrency.isSelected = true
            viewBinding.transferCurrency.isSelected = false
        }
        viewBinding.transferCurrency.setOnSingleClickListener {
            Logger.debug("Transfer currency selected")
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
                    {
                        when (it) {
                            is DccViewModel.PaymentIntentResult.Success -> {
                                finishWithPaymentIntent(paymentIntentId = it.paymentIntentId)
                            }
                            is DccViewModel.PaymentIntentResult.Error -> {
                                finishWithPaymentIntent(exception = it.exception)
                            }
                        }
                    }
                )
        }
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        exception: Exception? = null
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        airwallex.handlePaymentData(requestCode, resultCode, data)
    }
}
