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

    private var currentPageNum = 0

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
            addItemDecoration(
                AirwallexDividerItemDecoration(
                    this@PaymentMethodsActivity,
                    R.drawable.airwallex_line_divider
                )
            )
        }

        srlPaymentMethods.setOnRefreshListener {
            fetchPaymentMethods()
        }
        srlPaymentMethods.setColorSchemeResources(
            R.color.airwallex_color_primary_default,
            R.color.airwallex_color_primary_dark_default,
            R.color.airwallex_color_accent_default
        )

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
        srlPaymentMethods.isRefreshing = true
        airwallex.getPaymentMethods(
            pageNum = currentPageNum,
            customerId = args.customerId,
            callback = object : Airwallex.GetPaymentMethodsCallback {
                override fun onSuccess(response: PaymentMethodResponse) {
                    srlPaymentMethods.isEnabled = response.hasMore
                    val cards = response.items.filter { it.type == PaymentMethodType.CARD }
                    srlPaymentMethods.isRefreshing = false
                    currentPageNum++
                    cardAdapter.setPaymentMethods(cards.reversed())
                    paymentNoCards.visibility =
                        if (cardAdapter.paymentMethods.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onFailed(exception: AirwallexException) {
                    srlPaymentMethods.isRefreshing = false
                    showError(exception.toString())
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
}