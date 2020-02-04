package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.Airwallex
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import com.airwallex.paymentacceptance.PaymentData.shipping
import com.neovisionaries.i18n.CountryCode
import kotlinx.android.synthetic.main.activity_start_pay.*
import okhttp3.*
import java.io.IOException
import java.util.*

class PaymentPayActivity : AppCompatActivity() {

    private val paymentIntentId: String by lazy {
        intent.getStringExtra(PAYMENT_INTENT_ID)
    }

    private val paymentAmount: Float by lazy {
        intent.getFloatExtra(PAYMENT_AMOUNT, 0F)
    }

    private var paymentMethod: PaymentMethod? = null

    companion object {

        private const val TAG = "PaymentPayActivity"

        private const val PAYMENT_INTENT_ID = "payment_intent_id"
        private const val PAYMENT_AMOUNT = "payment_amount"

        private const val REQUEST_EDIT_SHIPPING_CODE = 998
        private const val REQUEST_PAYMENT_METHOD_CODE = 999
        const val REQUEST_START_PAYMENT = 1000

        fun startActivityForResult(
            activity: Activity,
            paymentIntentId: String,
            amount: Float
        ) {
            val intent = Intent(activity, PaymentPayActivity::class.java)
            intent.putExtra(PAYMENT_INTENT_ID, paymentIntentId)
            intent.putExtra(PAYMENT_AMOUNT, amount)
            activity.startActivityForResult(intent, REQUEST_START_PAYMENT)
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
            EditShippingActivity.startActivityForResult(this, REQUEST_EDIT_SHIPPING_CODE)
        }

        rlPaymentMethod.setOnClickListener {
            PaymentMethodsActivity.startActivityForResult(this, REQUEST_PAYMENT_METHOD_CODE)
        }

        rlPlay.setOnClickListener {
            paymentMethod?.let {
                if (it.type == PaymentMethodType.CARD
                    && it.card?.number == null
                ) {
                    // Should fill cvc
                    val input = EditText(this)
                    input.inputType = InputType.TYPE_CLASS_NUMBER
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.input_cvc)
                        .setView(input)
                        .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            val cvc = input.text.toString().trim()
                            if (TextUtils.isEmpty(cvc)) {
                                Toast.makeText(
                                    this@PaymentPayActivity,
                                    "CVC can't be empty!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setPositiveButton
                            }
                            startConfirmPaymentIntent(it, cvc)
                        }
                    builder.show()
                } else {
                    startConfirmPaymentIntent(it)
                }
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

    private fun startConfirmPaymentIntent(paymentMethod: PaymentMethod, cvc: String? = null) {
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

        if (paymentMethod.type == PaymentMethodType.CARD
            && paymentMethod.card?.number == null
        ) {
            paymentIntentParams = PaymentIntentParams.Builder()
                .setRequestId(UUID.randomUUID().toString())
                .setDevice(device)
                .setPaymentMethodReference(
                    PaymentMethodReference.Builder()
                        .setId(paymentMethod.id)
                        .setCvc(cvc)
                        .build()
                )
                .setPaymentMethodOptions(paymentMethodOptions)
                .build()

        } else {
            paymentIntentParams = PaymentIntentParams.Builder()
                .setRequestId(UUID.randomUUID().toString())
                .setDevice(device)
                .setPaymentMethod(paymentMethod)
                .setPaymentMethodOptions(paymentMethodOptions)
                .build()
        }

        // Start Confirm PaymentIntent
        val airwallex = Airwallex(Store.token)
        airwallex.confirmPaymentIntent(
            paymentIntentId = paymentIntentId,
            paymentIntentParams = paymentIntentParams,
            callback = object : Airwallex.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    when (paymentMethod.type) {
                        PaymentMethodType.WECHAT -> {
                            handlePaymentWithWechat(airwallex, paymentIntent)
                        }
                        PaymentMethodType.CARD -> {
                            handlePaymentWithCard(airwallex)
                        }
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    loading.visibility = View.GONE
                    Toast.makeText(
                        this@PaymentPayActivity,
                        exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        )
    }

    private fun handlePaymentWithWechat(airwallex: Airwallex, paymentIntent: PaymentIntent) {
        val nextAction = paymentIntent.nextAction
        if (nextAction?.data == null
        ) {
            Toast.makeText(
                this@PaymentPayActivity,
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
                        this@PaymentPayActivity,
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
                context = this@PaymentPayActivity,
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
                            this@PaymentPayActivity,
                            "errCode $errCode, errMessage $errMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onCancel() {
                        Log.e(TAG, "User cancel the Wechat payment")
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@PaymentPayActivity,
                            "User cancel the payment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun handlePaymentWithCard(airwallex: Airwallex) {
        retrievePaymentIntent(airwallex)
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
                        Toast.makeText(
                            this@PaymentPayActivity,
                            "Payment Success!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@PaymentPayActivity,
                            "Payment failed! PaymentIntent status: ${paymentIntent.status}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    Log.e(TAG, "Retrieve PaymentIntent failed")
                    loading.visibility = View.GONE
                    Toast.makeText(
                        this@PaymentPayActivity,
                        exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateShippingLabel(shipping: PaymentMethod.Billing) {
        tvShipping.text = "${shipping.lastName} ${shipping.firstName}\n" +
                "${shipping.address?.street}\n" +
                "${shipping.address?.city}, ${shipping.address?.state}, ${CountryCode.values().find { it.name == shipping.address?.countryCode }?.getName()}"
        tvShipping.setTextColor(Color.parseColor("#2A2A2A"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        when (requestCode) {
            REQUEST_EDIT_SHIPPING_CODE -> {
                shipping =
                    data.getParcelableExtra<Parcelable>(EditShippingActivity.SHIPPING_DETAIL) as PaymentMethod.Billing
                shipping?.let {
                    updateShippingLabel(it)
                }
            }
            REQUEST_PAYMENT_METHOD_CODE -> {
                paymentMethod =
                    data.getParcelableExtra(PaymentMethodsActivity.PAYMENT_METHOD) as PaymentMethod
                paymentMethod?.let {
                    tvPaymentMethod.text = it.type?.value
                    tvPaymentMethod.setTextColor(Color.parseColor("#2A2A2A"))
                }
            }
        }
        rlPlay.isEnabled = shipping != null && paymentMethod != null
        btnPlay.isEnabled = rlPlay.isEnabled
    }
}