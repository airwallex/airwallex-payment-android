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

    private val token: String by lazy {
        intent.getStringExtra(TOKEN)
    }

    private val clientSecret: String by lazy {
        intent.getStringExtra(CLIENT_SECRET)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(token, clientSecret)
    }

    companion object {
        const val REQUEST_ADD_CARD_CODE = 98
        const val PAYMENT_METHOD = "payment_method"

        fun startActivityForResult(activity: Activity, token: String, clientSecret: String) {
            activity.startActivityForResult(
                Intent(activity, AddPaymentMethodActivity::class.java)
                    .putExtra(TOKEN, token)
                    .putExtra(CLIENT_SECRET, clientSecret),
                REQUEST_ADD_CARD_CODE
            )
        }
    }

    override fun onActionSave() {
        val card = cardWidget.paymentMethodCard ?: return
        val billing = billingWidget.billing ?: return

        loading.visibility = View.VISIBLE
        val paymentMethodParams = PaymentMethodParams.Builder()
            .setRequestId(UUID.randomUUID().toString())
            .setType(PaymentMethodType.CARD.code)
            .setCard(card)
            .setBilling(billing)
            .build()

        airwallex.createPaymentMethod(
            paymentMethodParams,
            object : Airwallex.PaymentMethodCallback {
                override fun onSuccess(paymentMethod: PaymentMethod) {
                    finishWithPaymentMethod(paymentMethod)
                }

                override fun onFailed(exception: AirwallexException) {
                    loading.visibility = View.GONE
                    showError(exception.toString())
                }
            })
    }

    private fun finishWithPaymentMethod(paymentMethod: PaymentMethod) {
        loading.visibility = View.GONE
        setResult(Activity.RESULT_OK, Intent().putExtra(PAYMENT_METHOD, paymentMethod))
        finish()
    }

    override fun menuEnable(): Boolean {
        return cardWidget.isValid && billingWidget.isValid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_add_card
        viewStub.inflate()

        cardWidget.cardChangeCallback = { invalidateOptionsMenu() }
        cardWidget.completionCallback = { billingWidget.requestFocus() }
        billingWidget.billingChangeCallback = { invalidateOptionsMenu() }
    }
}