package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.Airwallex
import com.airwallex.android.ContinuePaymentIntentParams
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentIntentContinueType
import kotlinx.android.synthetic.main.activity_select_currency.*
import java.math.BigDecimal

/**
 * Allow the customer to select one of the payment methods, or add a new one via [AddPaymentMethodActivity].
 *
 */
internal class SelectCurrencyActivity : AirwallexActivity() {

    private val airwallex: Airwallex by lazy {
        Airwallex()
    }

    private val args: SelectCurrencyActivityLaunch.Args by lazy {
        SelectCurrencyActivityLaunch.Args.getExtra(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        current_currency.text = args.paymentIntent.currency
        current_price.text = String.format(
            "%.2f",
            args.paymentIntent.amount.setScale(2, BigDecimal.ROUND_DOWN).toDouble()
        )

        transfer_currency.text = args.dcc.currency
        transfer_price.text = String.format(
            "%.2f",
            args.dcc.amount?.setScale(2, BigDecimal.ROUND_DOWN)?.toDouble()
        )

        rate.text = getString(R.string.rate, args.paymentIntent.currency, args.dcc.clientRate, args.dcc.currency)

        current_card.isSelected = true
        current_card.setOnClickListener {
            current_card.isSelected = true
            transfer_card.isSelected = false
        }
        transfer_card.setOnClickListener {
            current_card.isSelected = false
            transfer_card.isSelected = true
        }

        btnConfirm.setOnClickListener {
            setLoadingProgress(true)
            val params = ContinuePaymentIntentParams(
                paymentIntentId = args.paymentIntent.id,
                clientSecret = args.clientSecret,
                type = PaymentIntentContinueType.DCC,
                useDcc = transfer_card.isSelected,
                device = args.device
            )
            airwallex.continuePaymentIntent(this, params, object : Airwallex.PaymentListener<PaymentIntent> {
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
}
