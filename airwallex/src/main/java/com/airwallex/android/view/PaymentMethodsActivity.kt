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

    private var pageNum = 0

    private val args: PaymentMethodsActivityStarter.Args by lazy {
        PaymentMethodsActivityStarter.Args.getExtra(intent)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(
            args.token,
            args.paymentIntent.clientSecret!!
        )
    }

    private val shouldShowWechatPay: Boolean
        get() {
            return args.paymentIntent.availablePaymentMethodTypes.contains(
                PaymentMethodType.WECHAT.type
            )
        }

    private val shouldShowCard: Boolean
        get() {
            return args.paymentIntent.availablePaymentMethodTypes.contains(
                PaymentMethodType.CARD.type
            )
        }

    private lateinit var paymentMethodsAdapter: PaymentMethodsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_payment_methods
        viewStub.inflate()

        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        paymentMethodsAdapter = PaymentMethodsAdapter(
            shouldShowWechatPay = shouldShowWechatPay,
            shouldShowCard = shouldShowCard
        )

        paymentMethodsAdapter.callback = object : PaymentMethodsAdapter.Callback {
            override fun onPaymentMethodClick(paymentMethod: PaymentMethod) {
                startPaymentCheckout(paymentMethod)
            }

            override fun onWechatClick(paymentMethod: PaymentMethod) {
                startPaymentCheckout(paymentMethod)
            }
        }

        rvPaymentMethods.apply {
            layoutManager = viewManager
            adapter = paymentMethodsAdapter
        }

        paymentMethodsAdapter.onLoadMoreCallback = {
            paymentMethodsAdapter.startLoadingMore(rvPaymentMethods)
            fetchPaymentMethods()
        }

        paymentMethodsAdapter.addOnScrollListener(rvPaymentMethods)

        addPaymentMethod.visibility = if (shouldShowCard) View.VISIBLE else View.GONE
        addPaymentMethod.setOnClickListener {
            startAddPaymentMethod()
        }
    }

    override fun onActionSave() {
        // Ignore
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun startAddPaymentMethod() {
        AddPaymentMethodActivityStarter(this@PaymentMethodsActivity)
            .startForResult(
                AddPaymentMethodActivityStarter.Args
                    .Builder()
                    .setPaymentIntent(args.paymentIntent)
                    .setToken(args.token)
                    .build()
            )
    }

    private fun startPaymentCheckout(paymentMethod: PaymentMethod, cvc: String? = null) {
        PaymentCheckoutActivityStarter(this@PaymentMethodsActivity)
            .startForResult(
                PaymentCheckoutActivityStarter.Args.Builder()
                    .setPaymentIntent(args.paymentIntent)
                    .setToken(args.token)
                    .setPaymentMethod(paymentMethod)
                    .setCvc(cvc)
                    .build()
            )
    }

    private fun fetchPaymentMethods() {
        if (!shouldShowCard) {
            return
        }

        airwallex.getPaymentMethods(
            pageNum = pageNum,
            pageSize = PAGE_SIZE,
            customerId = args.paymentIntent.customerId,
            callback = object : Airwallex.GetPaymentMethodsCallback {
                override fun onSuccess(response: PaymentMethodResponse) {
                    paymentMethodsAdapter.endLoadingMore()
                    val cards = response.items.filter { it.type == PaymentMethodType.CARD }
                    paymentMethodsAdapter.setPaymentMethods(cards, response.hasMore)
                    paymentNoCards.visibility =
                        if (paymentMethodsAdapter.isEmpty()) View.VISIBLE else View.GONE
                    pageNum++
                }

                override fun onFailed(exception: AirwallexException) {
                    alert(message = exception.error?.message ?: exception.toString())
                    paymentMethodsAdapter.endLoadingMore()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            AddPaymentMethodActivityStarter.REQUEST_CODE -> {
                val result = AddPaymentMethodActivityStarter.Result.fromIntent(data)
                result?.let {
                    paymentMethodsAdapter.addNewPaymentMethod(it.paymentMethod)
                    paymentNoCards.visibility =
                        if (paymentMethodsAdapter.isEmpty()) View.VISIBLE else View.GONE
                    rvPaymentMethods.requestLayout()
                    startPaymentCheckout(it.paymentMethod, it.cvc)
                }
            }
            PaymentCheckoutActivityStarter.REQUEST_CODE -> {
                val result = PaymentCheckoutActivityStarter.Result.fromIntent(data)
                result?.let {
                    setResult(
                        Activity.RESULT_OK,
                        Intent().putExtras(
                            PaymentMethodsActivityStarter.Result(
                                it.paymentIntent,
                                it.paymentMethodType,
                                it.error
                            ).toBundle()
                        )
                    )
                    finish()
                }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}