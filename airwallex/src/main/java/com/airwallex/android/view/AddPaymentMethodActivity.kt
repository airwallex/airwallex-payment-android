package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodParams
import com.airwallex.android.model.PaymentMethodType
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_airwallex.*
import java.util.*

class AddPaymentMethodActivity : AirwallexActivity() {

    private val args: AddPaymentMethodActivityStarter.Args by lazy {
        AddPaymentMethodActivityStarter.Args.getExtra(intent)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(
            requireNotNull(args.customerSessionConfig.token),
            requireNotNull(args.customerSessionConfig.paymentIntent.clientSecret)
        )
    }

    override fun onActionSave() {
        val card = cardWidget.paymentMethodCard ?: return
        loading.visibility = View.VISIBLE
        val paymentMethodParams = PaymentMethodParams.Builder()
            .setCustomerId(args.customerSessionConfig.paymentIntent.customerId)
            .setRequestId(UUID.randomUUID().toString())
            .setType(PaymentMethodType.CARD.type)
            .setCard(card)
            .setBilling(billingWidget.billing)
            .build()

        airwallex.createPaymentMethod(
            paymentMethodParams,
            object : Airwallex.PaymentMethodCallback {
                override fun onSuccess(paymentMethod: PaymentMethod) {
                    onActionSave(paymentMethod, card.cvc!!)
                }

                override fun onFailed(exception: AirwallexException) {
                    loading.visibility = View.GONE
                    alert(message = exception.toString())
                }
            })
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    private fun onActionSave(paymentMethod: PaymentMethod, cvc: String) {
        loading.visibility = View.GONE
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtras(AddPaymentMethodActivityStarter.Result(paymentMethod, cvc).toBundle())
        )
        finish()
    }

    private fun isValid(): Boolean {
        return cardWidget.isValid && billingWidget.isValid
    }


    private fun invalidateConfirmStatus() {
        tvSaveCard.isEnabled = isValid()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_add_card
        viewStub.inflate()

        cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        billingWidget.shipping = args.customerSessionConfig.paymentIntent.order.shipping
        billingWidget.billingChangeCallback = { invalidateConfirmStatus() }

        tvSaveCard.isEnabled = isValid()
        tvSaveCard.setOnClickListener { onActionSave() }
    }
}