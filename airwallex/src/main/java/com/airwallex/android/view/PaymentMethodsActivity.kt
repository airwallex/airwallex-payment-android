package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodResponse
import com.airwallex.android.model.PaymentMethodType
import kotlinx.android.synthetic.main.activity_airwallex.*
import kotlinx.android.synthetic.main.activity_payment_methods.*

internal class PaymentMethodsActivity : AirwallexActivity() {

    private val args: PaymentMethodsActivityStarter.Args by lazy {
        PaymentMethodsActivityStarter.Args.getExtra(intent)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(args.token, args.clientSecret)
    }

    private val shouldShowWechatPay: Boolean
        get() {
            return args.shouldShowWechatPay
        }

    private lateinit var cardAdapter: PaymentMethodsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_payment_methods
        viewStub.inflate()

        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cardAdapter = PaymentMethodsAdapter(
            selectedPaymentMethod = args.paymentMethod,
            shouldShowWechatPay = shouldShowWechatPay
        )

        cardAdapter.callback = object : PaymentMethodsAdapter.Callback {
            override fun onPaymentMethodClick(paymentMethod: PaymentMethod) {
                finishWithPaymentMethod(paymentMethod)
            }

            override fun onWechatClick(paymentMethod: PaymentMethod) {
                finishWithPaymentMethod(paymentMethod)
            }

            override fun onAddCardClick() {
                startAddPaymentMethod()
            }
        }

        rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = cardAdapter
        }

        fetchPaymentMethods()
    }

    override fun onActionSave() {
        // Ignore
    }

    private fun startAddPaymentMethod() {
        AddPaymentCardActivityStarter(this@PaymentMethodsActivity)
            .startForResult(
                AddPaymentCardActivityStarter.Args.Builder()
                    .setClientSecret(args.clientSecret)
                    .setToken(args.token)
                    .setCustomerId(args.customerId)
                    .build()
            )
    }

    private fun finishWithPaymentMethod(paymentMethod: PaymentMethod) {
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtras(
                    PaymentMethodsActivityStarter.Result(
                        paymentMethod
                    ).toBundle()
                )
        )
        finish()
    }

    private fun fetchPaymentMethods() {
        pbLoading.visibility = View.VISIBLE
        airwallex.getPaymentMethods(
            pageSize = PAGE_SIZE,
            customerId = args.customerId,
            callback = object : Airwallex.GetPaymentMethodsCallback {
                override fun onSuccess(response: PaymentMethodResponse) {
                    val cards = response.items.filter { it.type == PaymentMethodType.CARD }
                    cardAdapter.setPaymentMethods(cards)
                    paymentNoCards.visibility =
                        if (cardAdapter.paymentMethods.isEmpty()) View.VISIBLE else View.GONE
                    pbLoading.visibility = View.GONE
                }

                override fun onFailed(exception: AirwallexException) {
                    showError(exception.toString())
                    pbLoading.visibility = View.GONE
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            AddPaymentCardActivityStarter.REQUEST_CODE -> {
                val result = AddPaymentCardActivityStarter.Result.fromIntent(data)
                result?.let {
                    finishWithPaymentMethod(it.paymentMethod)
                }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 100
    }
}