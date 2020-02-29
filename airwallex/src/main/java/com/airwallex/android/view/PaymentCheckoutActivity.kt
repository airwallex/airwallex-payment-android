package com.airwallex.android.view

import android.os.Bundle
import com.airwallex.android.R
import kotlinx.android.synthetic.main.activity_airwallex.*
import kotlinx.android.synthetic.main.activity_payment_checkout.*

internal class PaymentCheckoutActivity : AirwallexActivity() {

    private val args: PaymentCheckoutActivityStarter.Args by lazy {
        PaymentCheckoutActivityStarter.Args.getExtra(intent)
    }

    override fun onActionSave() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_payment_checkout
        viewStub.inflate()


        tvTotalPrice.text = String.format("$%.2f", args.customerSessionConfig.paymentIntent.amount)
    }
}