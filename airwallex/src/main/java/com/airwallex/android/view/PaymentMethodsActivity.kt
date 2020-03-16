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
import com.airwallex.android.model.*
import kotlinx.android.synthetic.main.activity_payment_methods.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Allow the customer to select from their attached payment methods, or add a new one via [AddPaymentMethodActivity].
 *
 */
internal class PaymentMethodsActivity : AirwallexCheckoutBaseActivity() {

    private var pageNum: AtomicInteger = AtomicInteger(0)

    private lateinit var paymentMethodsAdapter: PaymentMethodsAdapter

    private val args: PaymentMethodsActivityStarter.Args by lazy {
        PaymentMethodsActivityStarter.Args.getExtra(intent)
    }

    override val paymentIntent: PaymentIntent by lazy {
        args.paymentIntent
    }
    override val requestThreeDSecure: Boolean by lazy {
        args.requestThreeDSecure
    }

    // Only wechat will confirm in this page
    override val cvc: String?
        get() = null

    override val airwallex: Airwallex by lazy {
        Airwallex(
            token = requireNotNull(args.token),
            clientSecret = requireNotNull(paymentIntent.clientSecret)
        )
    }

    private val shouldShowWechatPay: Boolean by lazy {
        paymentIntent.availablePaymentMethodTypes.contains(
            PaymentMethodType.WECHAT.type
        )
    }

    private val shouldShowCard: Boolean by lazy {
        paymentIntent.availablePaymentMethodTypes.contains(
            PaymentMethodType.CARD.type
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        paymentMethodsAdapter = PaymentMethodsAdapter(
            shouldShowWechatPay = shouldShowWechatPay,
            shouldShowCard = shouldShowCard
        )

        paymentMethodsAdapter.listener = object : PaymentMethodsAdapter.Listener {
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

    override val layoutResource: Int
        get() = R.layout.activity_payment_methods

    private fun fetchPaymentMethods() {
        if (!shouldShowCard) {
            return
        }

        airwallex.getPaymentMethods(
            pageNum = pageNum.get(),
            pageSize = PAGE_SIZE,
            customerId = requireNotNull(paymentIntent.customerId),
            listener = object : Airwallex.PaymentListener<PaymentMethodResponse> {
                override fun onSuccess(response: PaymentMethodResponse) {
                    paymentMethodsAdapter.endLoadingMore()
                    val cards = response.items.filter { it.type == PaymentMethodType.CARD }
                    paymentMethodsAdapter.setPaymentMethods(cards, response.hasMore)
                    paymentNoCards.visibility =
                        if (paymentMethodsAdapter.isEmpty()) View.VISIBLE else View.GONE
                    pageNum.incrementAndGet()
                }

                override fun onFailed(exception: AirwallexException) {
                    alert(message = exception.error?.message ?: exception.toString())
                    paymentMethodsAdapter.endLoadingMore()
                }
            })
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun startAddPaymentMethod() {
        AddPaymentMethodActivityStarter(this@PaymentMethodsActivity)
            .startForResult(
                AddPaymentMethodActivityStarter.Args
                    .Builder()
                    .setShipping(paymentIntent.order.shipping)
                    .setCustomerId(requireNotNull(paymentIntent.customerId))
                    .setClientSecret(requireNotNull(paymentIntent.clientSecret))
                    .setToken(args.token)
                    .build()
            )
    }

    private fun startPaymentCheckout(paymentMethod: PaymentMethod, cvc: String? = null) {
        if (args.includeCheckoutFlow) {
            if (paymentMethod.type == PaymentMethodType.WECHAT) {
                // Confirm API is directly called by Wechat
                confirmPaymentIntent(
                    paymentMethod = paymentMethod,
                    callback = object : Airwallex.PaymentListener<PaymentIntent> {
                        override fun onSuccess(response: PaymentIntent) {
                            finishWithPaymentIntent(
                                paymentIntent = response,
                                type = paymentMethod.type
                            )
                        }

                        override fun onFailed(exception: AirwallexException) {
                            finishWithPaymentIntent(error = exception.error)
                        }
                    }
                )
            } else {
                // Start [PaymentCheckoutActivity] to start confirm PaymentIntent
                PaymentCheckoutActivityStarter(this@PaymentMethodsActivity)
                    .startForResult(
                        PaymentCheckoutActivityStarter.Args.Builder()
                            .setPaymentIntent(paymentIntent)
                            .setToken(args.token)
                            .setPaymentMethod(paymentMethod)
                            .setCvc(cvc)
                            .setRequestThreeDSecure(requestThreeDSecure)
                            .build()
                    )
            }
        } else {
            finishWithPaymentMethod(
                paymentMethod = paymentMethod,
                cvc = cvc
            )
        }
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
                    finishWithPaymentIntent(it.paymentIntent, it.paymentMethodType, it.error)
                }
            }
        }
    }

    private fun finishWithPaymentMethod(
        paymentMethod: PaymentMethod,
        cvc: String?
    ) {
        setResult(
            Activity.RESULT_OK, Intent().putExtras(
                PaymentMethodsActivityStarter.Result(
                    paymentMethod = paymentMethod,
                    cvc = cvc
                ).toBundle()
            )
        )
        finish()
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        type: PaymentMethodType? = null,
        error: AirwallexError? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK, Intent().putExtras(
                PaymentMethodsActivityStarter.Result(
                    paymentIntent = paymentIntent,
                    paymentMethodType = type,
                    error = error
                ).toBundle()
            )
        )
        finish()
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
