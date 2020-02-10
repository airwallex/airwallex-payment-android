package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodParams
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_airwallex.*
import java.util.*

class AddPaymentMethodActivity : AirwallexActivity() {

    private val token: String by lazy {
        intent.getStringExtra(TOKEN)
    }

    companion object {
        fun startActivityForResult(activity: Activity, token: String, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, AddPaymentMethodActivity::class.java)
                    .apply {
                        putExtra(TOKEN, token)
                    },
                requestCode
            )
        }
    }

    override fun onActionSave() {
        pbLoading.visibility = View.VISIBLE
        val card = cardWidget.paymentMethodCard ?: return

        val paymentMethodParams = PaymentMethodParams.Builder()
            .setRequestId(UUID.randomUUID().toString())
            .setType("card")
            .setCard(card)
            .setBilling(billingWidget.billing)
            .build()

        val airwallex = Airwallex(token)
        airwallex.createPaymentMethod(
            paymentMethodParams,
            object : Airwallex.PaymentMethodCallback {
                override fun onSuccess(paymentMethod: PaymentMethod) {
                    pbLoading.visibility = View.GONE
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                override fun onFailed(exception: AirwallexException) {
                    pbLoading.visibility = View.GONE
                    Toast.makeText(
                        this@AddPaymentMethodActivity,
                        exception.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            })
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