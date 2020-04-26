package com.airwallex.paymentacceptance

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.RetrievePaymentIntentParams
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_payment_cart.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class PaymentCartActivity : AppCompatActivity() {

    private val compositeSubscription = CompositeDisposable()

    private var dialog: Dialog? = null

    val airwallexStarter by lazy {
        AirwallexStarter(this@PaymentCartActivity)
    }

    private val api: Api
        get() {
            return ApiFactory(Settings.baseUrl).buildRetrofit().create(Api::class.java)
        }

    private val authApi: AuthApi
        get() {
            return ApiFactory(Settings.authUrl).buildRetrofit().create(AuthApi::class.java)
        }

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

        airwallexStarter.onDestroy()
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
                    Settings.token = null
                    setLoadingProgress(true)
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    val responseData = JSONObject(it.string())
                    Settings.token = responseData["token"].toString()

                    if (TextUtils.isEmpty(Settings.cachedCustomerId)) {
                        api.createCustomer(
                            mutableMapOf(
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
                        Observable.just(Settings.cachedCustomerId)
                    }
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    val customerId = if (it is String) {
                        it
                    } else {
                        val responseData = JSONObject((it as ResponseBody).string())
                        val customerId = responseData["id"].toString()
                        Settings.cachedCustomerId = customerId
                        customerId
                    }

                    val products = (cartFragment as PaymentCartFragment).products
                    val shipping = (cartFragment as PaymentCartFragment).shipping
                    api.createPaymentIntent(
                        mutableMapOf(
                            "request_id" to UUID.randomUUID().toString(),
//                            "amount" to products.sumByDouble { product ->
//                                product.unitPrice ?: 0 * (product.quantity ?: 0).toDouble()
//                            },
                            "amount" to Settings.price.toDouble(),
                            "currency" to Settings.currency,
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
                    {
                        if (it is HttpException) {
                            showCreatePaymentIntentError(
                                it.response()?.errorBody()?.string() ?: it.localizedMessage
                            )
                        } else {
                            showCreatePaymentIntentError(it.localizedMessage)
                        }
                    }
                )
        )
    }

    /**
     * PaymentIntent must come from merchant's server
     */
    private fun handlePaymentIntentResponse(paymentIntent: PaymentIntent) {
        airwallexStarter.presentPaymentFlow(
            paymentIntent,
            object : AirwallexStarter.PaymentIntentListener {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    when (paymentIntent.paymentMethodType) {
                        PaymentMethodType.WECHAT -> {
                            val weChat = paymentIntent.weChat
                            if (weChat == null) {
                                showPaymentError("Server error, WeChat data is null...")
                                return
                            }

                            val prepayId = weChat.prepayId
                            // We use the `URL mock` method to simulate WeChat Pay in the `Staging` environment.
                            // By requesting this URL, we will set the status of the `PaymentIntent` to success.
                            if (prepayId?.startsWith("http") == true) {
                                // **This is just for test on Staging env**
                                Log.d(
                                    TAG,
                                    "Confirm PaymentIntent success, MOCK WeChat Pay on staging env."
                                )
                                // MOCK WeChat Pay
                                val client = OkHttpClient()
                                val builder = Request.Builder()
                                builder.url(prepayId)
                                client.newCall(builder.build()).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        runOnUiThread {
                                            Log.e(TAG, "Mock WeChat Pay failed, reason: $e.message")
                                            showPaymentError(e.message)
                                        }
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        runOnUiThread {
                                            if (response.isSuccessful) {
                                                Log.d(TAG, "Mock WeChat Pay successful.")
                                                showPaymentSuccess()
                                            } else {
                                                Log.e(TAG, "Mock WeChat Pay failed.")
                                                showPaymentError("Mock WeChat Pay failed.")
                                            }
                                        }
                                    }
                                })
                            } else {
                                Log.d(TAG, "Confirm PaymentIntent success, launch REAL WeChat Pay.")
                                // Launch WeChat Pay
                                WXPay.instance.launchWeChat(
                                    context = this@PaymentCartActivity,
                                    appId = Settings.wechatAppId,
                                    data = weChat,
                                    listener = object : WXPay.WeChatPaymentListener {
                                        override fun onSuccess() {
                                            Log.d(TAG, "REAL WeChat Pay successful.")
                                            showPaymentSuccess()
                                        }

                                        override fun onFailure(
                                            errCode: String?,
                                            errMessage: String?
                                        ) {
                                            Log.e(
                                                TAG,
                                                "REAL WeChat Pay failed, reason: $errMessage"
                                            )
                                            showPaymentError(errMessage)
                                        }

                                        override fun onCancel() {
                                            Log.d(TAG, "REAL WeChat Pay cancelled.")
                                            showPaymentError("REAL WeChat Pay cancelled.")
                                        }
                                    })
                            }
                        }
                        PaymentMethodType.CARD -> {
                            showPaymentSuccess()
                        }
                    }
                }

                override fun onFailed(error: AirwallexError) {
                    showPaymentError(error.message)
                }

                override fun onCancelled() {
                    Log.d(TAG, "User cancel the payment")
                    showPaymentCancelled()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airwallexStarter.handlePaymentResult(requestCode, resultCode, data)
    }

    /**
     * After successful payment, Airwallex server will notify the Merchant,
     * Then Merchant can retrieve the `PaymentIntent` to check the `status` of the PaymentIntent.
     */
    private fun retrievePaymentIntent(
        airwallex: Airwallex,
        paymentIntentId: String,
        clientSecret: String
    ) {
        airwallex.retrievePaymentIntent(
            params = RetrievePaymentIntentParams(
                // the ID of the `PaymentIntent`, required.
                paymentIntentId = paymentIntentId,
                // the clientSecret of `PaymentIntent`, required.
                clientSecret = clientSecret
            ),
            listener = object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onSuccess(response: PaymentIntent) {
                    if (response.status == PaymentIntentStatus.SUCCEEDED) {
                        // payment successful
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                }
            })
    }

    private fun setLoadingProgress(loading: Boolean) {
        if (loading) {
            startWait(this)
        } else {
            endWait()
        }
    }

    private fun showPaymentSuccess() {
        setLoadingProgress(false)
        showAlert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        )
    }

    private fun showCreatePaymentIntentError(error: String? = null) {
        setLoadingProgress(false)
        showAlert(
            getString(R.string.create_payment_intent_failed),
            error ?: getString(R.string.payment_failed_message)
        )
    }

    private fun showPaymentError(error: String? = null) {
        setLoadingProgress(false)
        showAlert(
            getString(R.string.payment_failed),
            error ?: getString(R.string.payment_failed_message)
        )
    }

    private fun showPaymentCancelled(error: String? = null) {
        setLoadingProgress(false)
        showAlert(
            getString(R.string.payment_cancelled),
            error ?: getString(R.string.payment_cancelled_message)
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

    private fun startWait(activity: Activity) {
        if (dialog?.isShowing == true) {
            return
        }
        if (!activity.isFinishing) {
            try {
                dialog = Dialog(activity)
                dialog?.setContentView(ProgressBar(this))
                dialog?.setCancelable(false)
                dialog?.show()
            } catch (e: Exception) {
                Log.d(TAG, "Failed to show loading dialog", e)
            }
        } else {
            dialog = null
        }
    }

    private fun endWait() {
        dialog?.dismiss()
        dialog = null
    }

    companion object {
        private const val TAG = "PaymentCartActivity"

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PaymentCartActivity::class.java))
            (context as Activity).finish()
        }
    }
}
