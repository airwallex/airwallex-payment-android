package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.airwallex.android.Airwallex
import com.airwallex.android.ContinuePaymentIntentParams
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentIntentContinueType
import kotlinx.android.synthetic.main.activity_select_currency.*

/**
 * Allow the customer to select your currency.
 */
internal class SelectCurrencyActivity : AirwallexActivity() {

    private val airwallex: Airwallex by lazy { Airwallex() }
    private val args: SelectCurrencyActivityLaunch.Args by lazy { SelectCurrencyActivityLaunch.Args.getExtra(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Current currency
        current_currency.updateCurrency(args.paymentIntent.currency, args.paymentIntent.amount)

        // Transfer currency
        val dccCurrency = args.dcc.currency
        val dccAmount = args.dcc.amount
        if (dccCurrency != null && dccAmount != null) {
            transfer_currency.updateCurrency(dccCurrency, dccAmount)
        } else {
            transfer_currency.visibility = View.GONE
        }

        rate.text = getString(R.string.rate, args.paymentIntent.currency, args.dcc.clientRate, args.dcc.currency)

        current_currency.isSelected = true
        current_currency.setOnClickListener {
            current_currency.isSelected = true
            transfer_currency.isSelected = false
        }
        transfer_currency.setOnClickListener {
            current_currency.isSelected = false
            transfer_currency.isSelected = true
        }

        confirm.setOnClickListener {
            setLoadingProgress(loading = true, cancelable = false)
            val params = ContinuePaymentIntentParams(
                paymentIntentId = args.paymentIntent.id,
                clientSecret = args.clientSecret,
                type = PaymentIntentContinueType.DCC,
                useDcc = transfer_currency.isSelected
            )
            airwallex.continuePaymentIntent(applicationContext, ThreeDSecureActivityLaunch(this), params, object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onFailed(exception: AirwallexException) {
                    finishWithPaymentIntent(error = exception.error)
                }

                override fun onSuccess(response: PaymentIntent) {
                    finishWithPaymentIntent(paymentIntent = response)
                }
            })
        }
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        error: AirwallexError? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK, Intent().putExtras(
            SelectCurrencyActivityLaunch.Result(
                paymentIntent = paymentIntent,
                error = error
            ).toBundle()
        ))
        finish()
    }

    override val layoutResource: Int
        get() = R.layout.activity_select_currency

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
