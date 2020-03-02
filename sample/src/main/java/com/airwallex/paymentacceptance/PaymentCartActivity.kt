package com.airwallex.paymentacceptance

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.Airwallex
import com.airwallex.android.CustomerSessionConfig
import com.airwallex.android.PaymentSession
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.Order
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.view.PaymentMethodsActivityStarter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_payment_cart.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class PaymentCartActivity : AppCompatActivity() {

    private val compositeSubscription = CompositeDisposable()

    private val api: Api by lazy {
        ApiFactory(Constants.BASE_URL).create()
    }

    private val prefs: SharedPreferences by lazy {
        application.getSharedPreferences(TAG, 0)
    }

    private var cachedCustomerId: String
        set(value) {
            prefs.edit()
                .putString(CUSTOMER_ID, value)
                .apply()
        }
        get() {
            return prefs.getString(CUSTOMER_ID, "") ?: ""
        }

    private lateinit var token: String
    private var paymentSession: PaymentSession? = null

    private var airwallex: Airwallex? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_cart)

        btnCheckout.setOnClickListener {
            authAndCreatePaymentIntent()
        }
    }

    override fun onDestroy() {
        compositeSubscription.dispose()
        super.onDestroy()
    }

    private fun authAndCreatePaymentIntent() {
        compositeSubscription.add(
            api.authentication(
                apiKey = Constants.API_KEY,
                clientId = Constants.CLIENT_ID
            )
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    loading.visibility = View.VISIBLE
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    val responseData = JSONObject(it.string())
                    token = responseData["token"].toString()

                    val customerId = cachedCustomerId
                    if (TextUtils.isEmpty(customerId)) {
                        api.createCustomer(
                            authorization = "Bearer $token",
                            params = mutableMapOf(
                                "request_id" to UUID.randomUUID().toString(),
                                "merchant_customer_id" to UUID.randomUUID().toString(),
                                "first_name" to "John",
                                "last_name" to "Doe",
                                "email" to "john.doe@airwallex.com",
                                "phone_number" to "13800000000",
                                "additional_info" to mapOf(
                                    "registered_via_social_media" to false,
                                    "registration_date" to "2019-09-18",
                                    "first_successful_order_date" to "2019-09-18"
                                ),
                                "metadata" to mapOf(
                                    "id" to 1
                                )
                            )
                        )
                    } else {
                        Observable.just(customerId)
                    }
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    val customerId = if (it is String) {
                        it
                    } else {
                        val responseData = JSONObject((it as ResponseBody).string())
                        val customerId = responseData["id"].toString()
                        cachedCustomerId = customerId
                        customerId
                    }

                    val products = (cartFragment as PaymentCartFragment).products
                    val shipping = (cartFragment as PaymentCartFragment).shipping
                    api.createPaymentIntent(
                        authorization = "Bearer $token",
                        params = mutableMapOf(
                            "request_id" to UUID.randomUUID().toString(),
                            "amount" to products.sumByDouble { product ->
                                product.unitPrice ?: 0 * (product.quantity ?: 0).toDouble()
                            },
                            "currency" to "USD",
                            "merchant_order_id" to UUID.randomUUID().toString(),
                            "order" to Order.Builder()
                                .setProducts(products)
                                .setShipping(shipping)
                                .setType("physical_goods")
                                .build(),
                            "customer_id" to customerId,
                            "descriptor" to "Airwallex - T-shirt",
                            "metadata" to mapOf("id" to 1)
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleResponse(it) },
                    { handleError(it) }
                )
        )
    }

    private fun handleError(err: Throwable) {
        loading.visibility = View.GONE
        Toast.makeText(this, err.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse(paymentIntent: PaymentIntent) {
        loading.visibility = View.GONE
        airwallex = Airwallex(token, paymentIntent.clientSecret!!)
        paymentSession = PaymentSession(this@PaymentCartActivity)
        paymentSession?.presentPaymentCheckoutFlow(CustomerSessionConfig(paymentIntent, token))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cartFragment.onActivityResult(requestCode, resultCode, data)

        paymentSession?.handlePaymentIntentResult(requestCode, resultCode, data,
            paymentIntentCallback = object :
                PaymentSession.PaymentResult<PaymentMethodsActivityStarter.Result> {
                override fun onCancelled() {
                    Log.d(TAG, "User cancel the payment checkout")
                }

                override fun onSuccess(result: PaymentMethodsActivityStarter.Result?) {
                    loading.visibility = View.VISIBLE
                    result?.let {
                        handlePaymentResult(it.paymentMethodType, it.paymentIntent) {
                            val paymentIntentId = it.paymentIntent.id
                            airwallex?.let { airwallex ->
                                retrievePaymentIntent(airwallex, paymentIntentId)
                            }
                        }
                    }
                }
            })
    }

    private fun handlePaymentResult(
        paymentMethodType: PaymentMethodType,
        paymentIntent: PaymentIntent,
        completion: () -> Unit
    ) {
        when (paymentMethodType) {
            PaymentMethodType.CARD -> {
                completion.invoke()
            }
            PaymentMethodType.WECHAT -> {
                val nextAction = paymentIntent.nextAction
                if (nextAction?.data == null
                ) {
                    Toast.makeText(
                        this@PaymentCartActivity,
                        "Server error, NextAction is null...",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val prepayId = nextAction.data?.prepayId

                Log.d(TAG, "prepayId $prepayId")

                if (prepayId?.startsWith("http") == true) {
                    Log.d(TAG, "Confirm PaymentIntent success, launch MOCK Wechat pay.")
                    // launch mock wechat pay
                    val client = OkHttpClient()
                    val builder = Request.Builder()
                    builder.url(prepayId)
                    client.newCall(builder.build()).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            loading.visibility = View.GONE
                            runOnUiThread {
                                Toast.makeText(
                                    this@PaymentCartActivity,
                                    "Failed to mock wechat pay, reason: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            completion.invoke()
                        }
                    })
                } else {
                    Log.d(TAG, "Confirm PaymentIntent success, launch REAL Wechat pay.")
                    val data = paymentIntent.nextAction?.data
                    if (data == null) {
                        Toast.makeText(
                            this@PaymentCartActivity,
                            "No Wechat data!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    // launch wechat pay
                    WXPay.instance.launchWeChat(
                        context = this@PaymentCartActivity,
                        appId = Constants.APP_ID,
                        data = data,
                        listener = object : WXPay.WechatPaymentListener {
                            override fun onSuccess() {
                                completion.invoke()
                            }

                            override fun onFailure(errCode: String?, errMessage: String?) {
                                Log.e(TAG, "Wechat pay failed, error $errMessage")
                                loading.visibility = View.GONE
                                Toast.makeText(
                                    this@PaymentCartActivity,
                                    "errCode $errCode, errMessage $errMessage",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onCancel() {
                                Log.e(TAG, "User cancel the Wechat payment")
                                loading.visibility = View.GONE
                                Toast.makeText(
                                    this@PaymentCartActivity,
                                    "User cancel the payment",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
            }
        }
    }

    private fun retrievePaymentIntent(airwallex: Airwallex, paymentIntentId: String) {
        Log.d(
            TAG,
            "Start Retrieve PaymentIntent"
        )
        airwallex.retrievePaymentIntent(
            paymentIntentId = paymentIntentId,
            callback = object : Airwallex.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    Log.d(
                        TAG,
                        "Retrieve PaymentIntent success, PaymentIntent status: ${paymentIntent.status}"
                    )

                    loading.visibility = View.GONE
                    if (paymentIntent.status == "SUCCEEDED") {
                        showPaymentSuccess()
                    } else {
                        showPaymentError()
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    Log.e(TAG, "Retrieve PaymentIntent failed", exception)
                    loading.visibility = View.GONE
                    showPaymentError()
                }
            })
    }

    fun showPaymentSuccess() {
        showAlert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        )
    }

    fun showPaymentError() {
        showAlert(
            getString(R.string.payment_failed),
            getString(R.string.payment_failed_message)
        )
    }

    private fun showAlert(title: String, message: String) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "PaymentCartActivity"
        private const val CUSTOMER_ID = "customerId"

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PaymentCartActivity::class.java))
            (context as Activity).finish()
        }
    }
}