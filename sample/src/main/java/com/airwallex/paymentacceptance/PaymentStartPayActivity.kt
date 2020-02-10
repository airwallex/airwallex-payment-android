package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.airwallex.android.Airwallex
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import com.airwallex.paymentacceptance.PaymentData.shipping
import kotlinx.android.synthetic.main.activity_start_pay.*
import okhttp3.*
import java.io.IOException
import java.util.*

class PaymentStartPayActivity : PaymentBaseActivity() {

    private val paymentIntentId: String by lazy {
        intent.getStringExtra(PAYMENT_INTENT_ID)
    }

    private val paymentAmount: Float by lazy {
        intent.getFloatExtra(PAYMENT_AMOUNT, 0F)
    }

    private var paymentMethod: PaymentMethod? = null

    companion object {

        private const val TAG = "PaymentPayActivity"

        private const val PAYMENT_AMOUNT = "payment_amount"


        fun startActivity(
            activity: Activity,
            paymentIntentId: String,
            amount: Float
        ) {
            activity.startActivity(
                Intent(activity, PaymentStartPayActivity::class.java)
                    .putExtra(PAYMENT_INTENT_ID, paymentIntentId)
                    .putExtra(PAYMENT_AMOUNT, amount)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_pay)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        tvTotalPrice.text = "$$paymentAmount"

        rlShipping.setOnClickListener {
            PaymentEditShippingActivity.startActivityForResult(this, REQUEST_EDIT_SHIPPING_CODE)
        }

        rlPaymentMethod.setOnClickListener {
            PaymentMethodsActivity.startActivityForResult(
                this,
                paymentMethod,
                paymentIntentId,
                REQUEST_PAYMENT_METHOD_CODE
            )
        }

        rlPlay.setOnClickListener {
            paymentMethod?.let {
                startConfirmPaymentIntent(it)
            }
        }

        if (shipping != null) {
            updateShippingLabel(shipping!!)
        } else {
            tvShipping.text = getString(R.string.select_shipping)
            tvShipping.setTextColor(Color.parseColor("#A9A9A9"))
        }

        if (paymentMethod == null) {
            tvPaymentMethod.text = getString(R.string.select_payment_method)
            tvPaymentMethod.setTextColor(Color.parseColor("#A9A9A9"))
        } else {
            tvPaymentMethod.text = paymentMethod!!.type?.value
            tvPaymentMethod.setTextColor(Color.parseColor("#2A2A2A"))
        }

        rlPlay.isEnabled = shipping != null && paymentMethod != null
        btnPlay.isEnabled = rlPlay.isEnabled
    }

    private fun startConfirmPaymentIntent(paymentMethod: PaymentMethod) {
        when (paymentMethod.type) {
            PaymentMethodType.CARD -> {
                // Need fill CVC
                PaymentConfirmCvcActivity.startActivityForResult(
                    this,
                    paymentMethod,
                    paymentIntentId,
                    REQUEST_CONFIRM_CVC_CODE
                )
            }
            PaymentMethodType.WECHAT -> {
                loading.visibility = View.VISIBLE
                val paymentIntentParams: PaymentIntentParams
                val device = Device.Builder()
                    .setBrowserInfo("Chrome/76.0.3809.100")
                    .setCookiesAccepted("true")
                    .setDeviceId("IMEI-4432fsdafd31243244fdsafdfd653")
                    .setHostName("www.airwallex.com")
                    .setHttpBrowserEmail("jim631@sina.com")
                    .setHttpBrowserType("chrome")
                    .setIpAddress("123.90.0.1")
                    .setIpNetworkAddress("128.0.0.0")
                    .build()

                val paymentMethodOptions: PaymentMethodOptions = PaymentMethodOptions.Builder()
                    .setCardOptions(
                        PaymentMethodOptions.CardOptions.Builder()
                            .setAutoCapture(true)
                            .setThreeDs(
                                PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                                    .setOption(false)
                                    .build()
                            ).build()
                    )
                    .build()

                paymentIntentParams = PaymentIntentParams.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setDevice(device)
                    .setPaymentMethod(paymentMethod)
                    .setPaymentMethodOptions(paymentMethodOptions)
                    .build()

                // Start Confirm PaymentIntent
                val airwallex = Airwallex(Store.token)
                airwallex.confirmPaymentIntent(
                    paymentIntentId = paymentIntentId,
                    paymentIntentParams = paymentIntentParams,
                    callback = object : Airwallex.PaymentIntentCallback {
                        override fun onSuccess(paymentIntent: PaymentIntent) {
                            handlePaymentWithWechat(airwallex, paymentIntent)
                        }

                        override fun onFailed(exception: AirwallexException) {
                            loading.visibility = View.GONE
                            Toast.makeText(
                                this@PaymentStartPayActivity,
                                exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                )
            }
        }
    }

    private fun handlePaymentWithWechat(airwallex: Airwallex, paymentIntent: PaymentIntent) {
        val nextAction = paymentIntent.nextAction
        if (nextAction?.data == null
        ) {
            Toast.makeText(
                this@PaymentStartPayActivity,
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
                    Log.e(TAG, "User cancel the Wechat payment")
                    loading.visibility = View.GONE
                    Toast.makeText(
                        this@PaymentStartPayActivity,
                        "Failed to mock wechat pay, reason: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    retrievePaymentIntent(airwallex)
                }
            })
        } else {
            Log.d(TAG, "Confirm PaymentIntent success, launch REAL Wechat pay.")
            // launch wechat pay
            WXPay.instance.launchWeChat(
                context = this@PaymentStartPayActivity,
                appId = Constants.APP_ID,
                data = paymentIntent.nextAction!!.data!!,
                listener = object : PayListener {
                    override fun onSuccess() {
                        retrievePaymentIntent(airwallex)
                    }

                    override fun onFailure(errCode: String?, errMessage: String?) {
                        Log.e(TAG, "Wechat pay failed, error $errMessage")
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@PaymentStartPayActivity,
                            "errCode $errCode, errMessage $errMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onCancel() {
                        Log.e(TAG, "User cancel the Wechat payment")
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@PaymentStartPayActivity,
                            "User cancel the payment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun retrievePaymentIntent(airwallex: Airwallex) {
        Log.d(
            TAG,
            "Start retrieve PaymentIntent $paymentIntentId"
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
                    Log.e(TAG, "Retrieve PaymentIntent failed")
                    loading.visibility = View.GONE

                    // TODO Need Retry?
                    showPaymentError()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateShippingLabel(shipping: Shipping) {
        val countryName = shipping.address?.countryCode?.let {
            val loc = Locale("", it)
            loc.displayCountry
        }

        tvShipping.text = "${shipping.lastName} ${shipping.firstName}\n" +
                "${shipping.address?.street}\n" +
                "${shipping.address?.city}, ${shipping.address?.state}, $countryName"
        tvShipping.setTextColor(Color.parseColor("#2A2A2A"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            REQUEST_EDIT_SHIPPING_CODE -> {
                shipping =
                    data.getParcelableExtra<Parcelable>(SHIPPING_DETAIL) as Shipping
                shipping?.let {
                    updateShippingLabel(it)
                }
            }
            REQUEST_PAYMENT_METHOD_CODE -> {
                paymentMethod =
                    data.getParcelableExtra(PAYMENT_METHOD) as PaymentMethod
                paymentMethod?.let {
                    if (it.type == PaymentMethodType.WECHAT) {
                        tvPaymentMethod.text = it.type?.value
                    } else {
                        tvPaymentMethod.text =
                            String.format("%s •••• %s", it.card?.brand, it.card?.last4)
                    }

                    tvPaymentMethod.setTextColor(Color.parseColor("#2A2A2A"))
                }
            }
        }
        rlPlay.isEnabled = shipping != null && paymentMethod != null
        btnPlay.isEnabled = rlPlay.isEnabled
    }
}