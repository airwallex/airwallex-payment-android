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

    // The minimum amount of items to have below your current scroll position
    private var lastVisibleItem = 0
    private var totalItemCount: Int = 0
    private var loading = false
    private var pageNum = 0
    private var hasMore = true

    private val args: PaymentMethodsActivityStarter.Args by lazy {
        PaymentMethodsActivityStarter.Args.getExtra(intent)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(args.token!!, args.paymentIntent!!.clientSecret)
    }

    private val shouldShowWechatPay: Boolean
        get() {
            return args.paymentIntent!!.availablePaymentMethodTypes.contains(
                PaymentMethodType.WECHAT.type
            )
        }

    private lateinit var cardAdapter: PaymentMethodsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_payment_methods
        viewStub.inflate()

        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cardAdapter = PaymentMethodsAdapter(
            shouldShowWechatPay = shouldShowWechatPay
        )

        cardAdapter.callback = object : PaymentMethodsAdapter.Callback {
            override fun onPaymentMethodClick(paymentMethod: PaymentMethod) {
                startPaymentConfirm(paymentMethod)
            }

            override fun onWechatClick(paymentMethod: PaymentMethod) {
                startPaymentConfirm(paymentMethod)
            }
        }

        rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = cardAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    totalItemCount = viewManager.itemCount
                    lastVisibleItem = viewManager.findLastVisibleItemPosition()
                    if (!loading && totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD && hasMore) {
                        fetchPaymentMethods(totalItemCount == 1)
                    }
                }
            })

        }

        addPaymentMethod.setOnClickListener {
            startAddPaymentMethod()
        }
    }

    override fun onActionSave() {
        // Ignore
    }

    private fun startAddPaymentMethod() {
        AddPaymentMethodActivityStarter(this@PaymentMethodsActivity)
            .startForResult(
                AddPaymentMethodActivityStarter.Args.Builder()
                    .setPaymentIntent(args.paymentIntent!!)
                    .setToken(args.token!!)
                    .build()
            )
    }

    private fun startPaymentConfirm(paymentMethod: PaymentMethod) {
        PaymentCheckoutActivityStarter(this)
            .start(
                PaymentCheckoutActivityStarter.Args.Builder()
                    .setPaymentMethod(paymentMethod)
                    .setPaymentIntent(args.paymentIntent!!)
                    .setToken(args.token!!)
                    .build()
            )
    }

    private fun fetchPaymentMethods(showLoading: Boolean = false) {
        if (showLoading) {
            pbLoading.visibility = View.VISIBLE
        }
        loading = true
        airwallex.getPaymentMethods(
            pageNum = pageNum,
            pageSize = PAGE_SIZE,
            customerId = args.paymentIntent!!.customerId!!,
            callback = object : Airwallex.GetPaymentMethodsCallback {
                override fun onSuccess(response: PaymentMethodResponse) {
                    val cards = response.items.filter { it.type == PaymentMethodType.CARD }
                    cardAdapter.setPaymentMethods(cards)
                    paymentNoCards.visibility =
                        if (cardAdapter.paymentMethods.isEmpty()) View.VISIBLE else View.GONE
                    pbLoading.visibility = View.GONE
                    hasMore = response.hasMore
                    pageNum++
                    loading = false
                }

                override fun onFailed(exception: AirwallexException) {
                    showError(exception.toString())
                    pbLoading.visibility = View.GONE
                    loading = false
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
                    startPaymentConfirm(it.paymentMethod)
                }
            }
        }
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 3
        private const val PAGE_SIZE = 20
    }
}