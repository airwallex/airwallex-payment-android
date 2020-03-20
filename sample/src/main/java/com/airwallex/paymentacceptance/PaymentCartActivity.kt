package com.airwallex.paymentacceptance

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.Airwallex
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentIntentStatus
import com.airwallex.android.model.PurchaseOrder
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

    private val handler: Handler = Handler()

    private val api: Api
        get() {
            return ApiFactory(Settings.baseUrl).buildRetrofit().create(Api::class.java)
        }

    private val authApi: AuthApi
        get() {
            return ApiFactory(Settings.authUrl).buildRetrofit().create(AuthApi::class.java)
        }

    private val prefs: SharedPreferences by lazy {
        application.getSharedPreferences(TAG, 0)
    }

    /**
     * Cache customerId is just to prevent creating multiple customers
     */
    private var cachedCustomerId: String
        set(value) {
            prefs.edit()
                .putString(CUSTOMER_ID, value)
                .apply()
        }
        get() {
            return prefs.getString(CUSTOMER_ID, "") ?: ""
        }

    // token cannot appear on the merchant side, this is just for Demo purposes only
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_cart)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle(R.string.app_name)

        btnCheckout.setOnClickListener {
            authAndCreatePaymentIntent()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reset -> {
                (cartFragment as PaymentCartFragment).reset()
                true
            }
            R.id.settings -> {
                startActivity(Intent(this, PaymentSettingsActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        compositeSubscription.dispose()
        super.onDestroy()
    }

    /**
     * `IMPORTANT` This code must be placed on the merchant server, this is just for Demo purposes only
     */
    private fun authAndCreatePaymentIntent() {
        compositeSubscription.add(
            authApi.authentication(
                apiKey = Settings.apiKey,
                clientId = Settings.clientId
            )
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    setLoadingProgress(true)
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
                            "order" to PurchaseOrder.Builder()
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
                    { handlePaymentIntentResponse(it) },
                    { showPaymentError(it.localizedMessage) }
                )
        )
    }

    /**
     * PaymentIntent must come from merchant's server, only wechat pay is currently supported
     */
    private fun handlePaymentIntentResponse(paymentIntent: PaymentIntent) {
        val airwallex = Airwallex(
            clientSecret = requireNotNull(paymentIntent.clientSecret),
            customerId = paymentIntent.customerId,
            baseUrl = Settings.baseUrl  // You can change the baseUrl to test other environments
        )
        airwallex.confirmPaymentIntent(
            paymentIntentId = paymentIntent.id,
            listener = object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onSuccess(response: PaymentIntent) {
                    val paymentIntentId = response.id
                    val nextActionData = response.nextAction?.data
                    if (nextActionData == null) {
                        showPaymentError("Server error, nextAction data is null...")
                        return
                    }

                    val prepayId = nextActionData.prepayId
                    if (prepayId?.startsWith("http") == true) {
                        Log.d(TAG, "Confirm PaymentIntent success, launch MOCK wechatpay.")
                        // launch mock wechat pay
                        val client = OkHttpClient()
                        val builder = Request.Builder()
                        builder.url(prepayId)
                        client.newCall(builder.build()).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                runOnUiThread {
                                    showPaymentError("Failed to mock wechat pay, reason: ${e.message}")
                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                handler.postDelayed({
                                    retrievePaymentIntent(airwallex, paymentIntentId)
                                }, 200)
                            }
                        })
                    } else {
                        Log.d(TAG, "Confirm PaymentIntent success, launch REAL wechatpay.")
                        // launch wechat pay
                        WXPay.instance.launchWeChat(
                            context = this@PaymentCartActivity,
                            appId = Settings.wechatAppId,
                            data = nextActionData,
                            listener = object : WXPay.WechatPaymentListener {
                                override fun onSuccess() {
                                    Log.d(
                                        TAG,
                                        "wechatpay successful, retrieve the payment intent status."
                                    )
                                    retrievePaymentIntent(airwallex, response.id)
                                }

                                override fun onFailure(errCode: String?, errMessage: String?) {
                                    showPaymentError(errMessage)
                                }

                                override fun onCancel() {
                                    showPaymentError("User cancel the payment")
                                }
                            })
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    showPaymentError(exception.error?.message)
                }
            })
    }

    /**
     * You can retrieve PaymentIntent to determine whether the PaymentIntent was successful by `status`
     */
    private fun retrievePaymentIntent(airwallex: Airwallex, paymentIntentId: String) {
        airwallex.retrievePaymentIntent(
            paymentIntentId = paymentIntentId,
            listener = object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onSuccess(response: PaymentIntent) {
                    if (response.status == PaymentIntentStatus.SUCCEEDED) {
                        showPaymentSuccess()
                    } else {
                        showPaymentError(response.status.name)
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    showPaymentError(exception.error?.message)
                }
            })
    }

    private fun setLoadingProgress(loading: Boolean) {
        loadingView.visibility = if (loading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun showPaymentSuccess() {
        setLoadingProgress(false)
        showAlert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        )
    }

    private fun showPaymentError(error: String? = null) {
        setLoadingProgress(false)
        showAlert(
            getString(R.string.payment_failed),
            error ?: getString(R.string.payment_failed_message)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cartFragment.onActivityResult(requestCode, resultCode, data)
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
