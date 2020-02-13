package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.view.AddPaymentMethodActivity
import com.airwallex.android.view.AddPaymentMethodActivity.Companion.REQUEST_ADD_CARD_CODE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_shipping.toolbar
import kotlinx.android.synthetic.main.activity_payment_methods.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class PaymentMethodsActivity : PaymentBaseActivity() {

    private val api: Api by lazy {
        ApiFactory(Constants.BASE_URL).create()
    }

    private val paymentIntent: PaymentIntent by lazy {
        intent.getParcelableExtra(PAYMENT_INTENT) as PaymentIntent
    }

    private val selectedPaymentMethod: PaymentMethod? by lazy {
        intent.getParcelableExtra(PAYMENT_METHOD) as? PaymentMethod
    }

    override val inPaymentFlow: Boolean
        get() = true

    private val compositeSubscription = CompositeDisposable()
    private lateinit var cardAdapter: PaymentMethodsAdapter
    private val paymentMethods = mutableListOf<PaymentMethod?>(null)

    companion object {
        private const val TAG = "PaymentMethodsActivity"

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

        fetchPaymentMethods()
    }

    fun onSaveResult() {
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(PAYMENT_METHOD, cardAdapter.selectedPaymentMethod)
        )
        finish()
    }

    override fun onDestroy() {
        compositeSubscription.dispose()
        super.onDestroy()
    }

    private fun fetchPaymentMethods() {
        compositeSubscription.add(
            // TODO Visa & Mastercard
            api.fetchPaymentMethods(authorization = "Bearer ${Store.token}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleResponse(it) },
                    { handleError(it) }
                )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            REQUEST_ADD_CARD_CODE -> {
                val paymentMethod =
                    data.getParcelableExtra<Parcelable>(AddPaymentMethodActivity.PAYMENT_METHOD) as PaymentMethod
                Log.d(TAG, "Save card success ${paymentMethod.id}")
                // Add card success
                fetchPaymentMethods()
            }
        }
    }

    private fun handleError(err: Throwable) {
        Toast.makeText(this, err.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse(responseBody: ResponseBody) {
        try {
            val responseData = JSONObject(responseBody.string())
            val items: List<PaymentMethod> = Gson().fromJson(
                responseData["items"].toString(),
                object : TypeToken<List<PaymentMethod?>?>() {}.type
            )

            val cards = items.filter { it.type == PaymentMethodType.CARD }
            paymentNoCards.visibility = if (cards.isEmpty()) View.VISIBLE else View.GONE

            paymentMethods.clear()
            paymentMethods.addAll(cards)
            paymentMethods.add(null)
            cardAdapter.notifyDataSetChanged()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}