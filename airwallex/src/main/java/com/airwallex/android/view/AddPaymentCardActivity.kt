package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.Address
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodParams
import com.airwallex.android.model.PaymentMethodType
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_airwallex.*
import java.util.*

class AddPaymentCardActivity : AirwallexActivity() {

    private val args: AddPaymentCardActivityStarter.CardArgs by lazy {
        AddPaymentCardActivityStarter.CardArgs.create(intent)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(args.token, args.clientSecret)
    }

    override fun onActionSave() {
        val card = cardWidget.paymentMethodCard ?: return
        // TODO Need to be removed. As the billing will be optional
        val billing = PaymentMethod.Billing.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .setPhone("13800000000")
            .setEmail("jim631@sina.com")
            .setAddress(
                Address.Builder()
                    .setCountryCode("CN")
                    .setState("Shanghai")
                    .setCity("Shanghai")
                    .setStreet("Pudong District")
                    .setPostcode("100000")
                    .build()
            )
            .build()

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
                    card.cvc?.let {
                        onActionSave(paymentMethod, it)
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    loading.visibility = View.GONE
                    showError(exception.toString())
                }
            })
    }

    private fun onActionSave(paymentMethod: PaymentMethod, cvc: String) {
        loading.visibility = View.GONE
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtras(AddPaymentCardActivityStarter.Result(paymentMethod, cvc).toBundle())
        )
        finish()
    }

    private fun isValid(): Boolean {
        return cardWidget.isValid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_add_card
        viewStub.inflate()

        cardWidget.cardChangeCallback = { tvSaveCard.isEnabled = isValid() }
        tvSaveCard.isEnabled = isValid()

        tvSaveCard.setOnClickListener {
            onActionSave()
        }
    }
}