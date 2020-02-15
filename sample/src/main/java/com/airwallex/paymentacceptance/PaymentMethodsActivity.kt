package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.Airwallex
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodResponse
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.view.AddPaymentCardActivity
import com.airwallex.android.view.AirwallexActivity.Companion.REQUEST_ADD_CARD_CODE
import kotlinx.android.synthetic.main.activity_edit_shipping.toolbar
import kotlinx.android.synthetic.main.activity_payment_methods.*

class PaymentMethodsActivity : PaymentBaseActivity() {

    private val paymentIntent: PaymentIntent by lazy {
        intent.getParcelableExtra(PAYMENT_INTENT) as PaymentIntent
    }

    private val selectedPaymentMethod: PaymentMethod? by lazy {
        intent.getParcelableExtra(PAYMENT_METHOD) as? PaymentMethod
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(Store.token, paymentIntent.clientSecret)
    }

    private var pageNum = 0

    override val inPaymentFlow: Boolean
        get() = true

    private lateinit var cardAdapter: PaymentMethodsAdapter
    private val paymentMethods = mutableListOf<PaymentMethod?>()

    companion object {

        fun startActivityForResult(
            activity: Activity,
            paymentMethod: PaymentMethod?,
            paymentIntent: PaymentIntent,
            requestCode: Int
        ) {
            activity.startActivityForResult(
                Intent(activity, PaymentMethodsActivity::class.java)
                    .putExtra(PAYMENT_METHOD, paymentMethod)
                    .putExtra(PAYMENT_INTENT, paymentIntent),
                requestCode
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cardAdapter = PaymentMethodsAdapter(
            paymentMethods,
            this,
            selectedPaymentMethod,
            paymentIntent
        )

        rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = cardAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@PaymentMethodsActivity,
                    R.drawable.line_divider
                )
            )
        }

        srlPaymentMethods.setOnRefreshListener {
            fetchPaymentMethods()
        }
        srlPaymentMethods.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorAccent
        )

        startFetchPaymentMethods()
    }

    private fun startFetchPaymentMethods() {
        fetchPaymentMethods()
        srlPaymentMethods.isRefreshing = true
    }

    fun onSavePaymentMethod(paymentMethod: PaymentMethod, cvc: String? = null) {
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(PAYMENT_METHOD, paymentMethod).putExtra(PAYMENT_CARD_CVC, cvc)
        )
        finish()
    }

    private fun fetchPaymentMethods() {
        airwallex.getPaymentMethods(
            pageNum = pageNum,
            callback = object : Airwallex.GetPaymentMethodsCallback {
                override fun onSuccess(response: PaymentMethodResponse) {
                    val cards = response.items.filter { it.type == PaymentMethodType.CARD }
                    paymentNoCards.visibility = if (cards.isEmpty()) View.VISIBLE else View.GONE

                    srlPaymentMethods.isRefreshing = false
                    pageNum++
                    this@PaymentMethodsActivity.paymentMethods.addAll(0, cards.reversed())
                    cardAdapter.notifyDataSetChanged()
                }

                override fun onFailed(exception: AirwallexException) {
                    srlPaymentMethods.isRefreshing = false
                    showError(getString(R.string.get_payment_methods_failed), exception.toString())
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            REQUEST_ADD_CARD_CODE -> {
                val paymentMethod =
                    data.getParcelableExtra<Parcelable>(AddPaymentCardActivity.PAYMENT_METHOD) as PaymentMethod
                val cvc = data.getStringExtra(AddPaymentCardActivity.PAYMENT_CARD_CVC)
                onSavePaymentMethod(paymentMethod, cvc)
            }
        }
    }
}