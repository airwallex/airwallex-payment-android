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

class PaymentMethodsActivity : AirwallexActivity() {

    private val args: PaymentMethodsActivityStarter.Args by lazy {
        PaymentMethodsActivityStarter.Args.create(intent)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(args.token!!, args.paymentIntent!!.clientSecret)
    }

    private var currentPageNum = 0

    private lateinit var cardAdapter: PaymentMethodsAdapter
    private val paymentMethods = mutableListOf<PaymentMethod>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_payment_methods
        viewStub.inflate()

        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cardAdapter = PaymentMethodsAdapter(
            paymentMethods,
            this,
            args.paymentMethod,
            args.paymentIntent!!,
            args.token!!
        )

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

        srlPaymentMethods.isRefreshing = true
        fetchPaymentMethods()
    }

    fun onSavePaymentMethod(paymentMethod: PaymentMethod, cvc: String? = null) {
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtras(
                    PaymentMethodsActivityStarter.Result(
                        paymentMethod,
                        cvc
                    ).toBundle()
                )
        )
        finish()
    }

    private fun fetchPaymentMethods() {
        airwallex.getPaymentMethods(
            pageNum = currentPageNum,
            callback = object : Airwallex.GetPaymentMethodsCallback {
                override fun onSuccess(response: PaymentMethodResponse) {
                    val cards = response.items.filter { it.type == PaymentMethodType.CARD }
                    paymentNoCards.visibility = if (cards.isEmpty()) View.VISIBLE else View.GONE

                    srlPaymentMethods.isRefreshing = false
                    currentPageNum++
                    this@PaymentMethodsActivity.paymentMethods.addAll(0, cards.reversed())
                    cardAdapter.notifyDataSetChanged()
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
                    onSavePaymentMethod(it.paymentMethod, it.cvc)
                }
            }
        }
    }
}