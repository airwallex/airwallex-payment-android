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
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.Airwallex
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import com.airwallex.paymentacceptance.PaymentData.paymentMethodType
import com.airwallex.paymentacceptance.PaymentData.shipping
import com.neovisionaries.i18n.CountryCode
import kotlinx.android.synthetic.main.activity_start_pay.*
import java.util.*

class PaymentPayActivity : AppCompatActivity() {

    private val paymentIntentId: String by lazy {
        intent.getStringExtra(PAYMENT_INTENT_ID)
    }

    private val paymentAmount: Float by lazy {
        intent.getFloatExtra(PAYMENT_AMOUNT, 0F)
    }

    private val token: String by lazy {
        intent.getStringExtra(PAYMENT_TOKEN)
    }

    companion object {

        private const val TAG = "PaymentPayActivity"

        private const val PAYMENT_INTENT_ID = "payment_intent_id"
        private const val PAYMENT_AMOUNT = "payment_amount"
        private const val PAYMENT_TOKEN = "payment_token"

        private const val REQUEST_EIDT_SHIPPING_CODE = 998
        private const val REQUEST_PAYMENT_MOTHOD_CODE = 999

        fun start(
            activity: Activity,
            paymentIntentId: String,
            amount: Float,
            token: String
        ) {
            val intent = Intent(activity, PaymentPayActivity::class.java)
            intent.putExtra(PAYMENT_INTENT_ID, paymentIntentId)
            intent.putExtra(PAYMENT_AMOUNT, amount)
            intent.putExtra(PAYMENT_TOKEN, token)
            activity.startActivity(intent)
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
            EditShippingActivity.startActivityForResult(this, REQUEST_EIDT_SHIPPING_CODE)
        }

        rlPaymentMethod.setOnClickListener {
            PaymentSelectMethodActivity.startActivityForResult(this, REQUEST_PAYMENT_MOTHOD_CODE)
        }

        rlPlay.setOnClickListener {
            loading.visibility = View.VISIBLE

            // Start Confirm PaymentIntent
            val airwallex = Airwallex(token)
            airwallex.confirmPaymentIntent(
                paymentIntentId = paymentIntentId,
                paymentIntentParams = PaymentIntentParams.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setDevice(
                        Device.Builder()
                            .setBrowserInfo("Chrome/76.0.3809.100")
                            .setCookiesAccepted("true")
                            .setDeviceId("IMEI-4432fsdafd31243244fdsafdfd653")
                            .setHostName("www.airwallex.com")
                            .setHttpBrowserEmail("jim631@sina.com")
                            .setHttpBrowserType("chrome")
                            .setIpAddress("123.90.0.1")
                            .setIpNetworkAddress("128.0.0.0")
                            .build()
                    )
                    .setPaymentMethod(
                        PaymentMethod.Builder()
                            .setType(PaymentMethodType.CARD)
                            .setCard(
                                PaymentMethod.Card.Builder()
                                    .setNumber("4012000300001003")
                                    .setExpMonth("12")
                                    .setExpYear("2020")
                                    .setCvc("123")
                                    .setName("Adam")
                                    .build()
                            )
                            .setBilling(shipping)
                            .build()
                    )
                    .build(),
                callback = object : Airwallex.PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        Log.d(TAG, "Confirm PaymentIntent success, start launch Wechat pay.")
                        // launch wechat pay
                        WXPay.instance.launchWeChat(
                            context = this@PaymentPayActivity,
                            appId = Constants.APP_ID,
                            listener = object : PayListener {
                                override fun onSuccess() {
                                    airwallex.retrievePaymentIntent(
                                        paymentIntentId = paymentIntentId,
                                        callback = object : Airwallex.PaymentIntentCallback {
                                            override fun onSuccess(paymentIntent: PaymentIntent) {
                                                Log.d(TAG, "Retrieve PaymentIntent success")
                                                loading.visibility = View.GONE

                                                Log.e(
                                                    TAG,
                                                    "paymentIntent.status ${paymentIntent.status}"
                                                )

                                                if (paymentIntent.status == "SUCCEEDED") {

                                                }

                                                Toast.makeText(
                                                    this@PaymentPayActivity,
                                                    "Payment Success!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                finish()
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

                    override fun onFailed(exception: AirwallexException) {
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@PaymentPayActivity,
                            exception.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            )
        }

        if (shipping != null) {
            updateShippingLabel(shipping!!)
        } else {
            tvShipping.text = getString(R.string.select_shipping)
            tvShipping.setTextColor(Color.parseColor("#A9A9A9"))
        }

        if (paymentMethodType == null) {
            tvPaymentMethod.text = getString(R.string.select_payment_method)
            tvPaymentMethod.setTextColor(Color.parseColor("#A9A9A9"))
        } else {
            tvPaymentMethod.text = paymentMethodType!!.value
            tvPaymentMethod.setTextColor(Color.parseColor("#2A2A2A"))
        }

        rlPlay.isEnabled = shipping != null && paymentMethodType != null
        btnPlay.isEnabled = rlPlay.isEnabled
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
            REQUEST_EIDT_SHIPPING_CODE -> {
                shipping =
                    data.getParcelableExtra<Parcelable>(EditShippingActivity.SHIPPING_DETAIL) as PaymentMethod.Billing
                shipping?.let {
                    updateShippingLabel(it)
                }
            }
            REQUEST_PAYMENT_MOTHOD_CODE -> {
                paymentMethodType =
                    data.getSerializableExtra(PaymentSelectMethodActivity.PAYMENT_METHOD_TYPE) as PaymentMethodType
                paymentMethodType?.let {
                    tvPaymentMethod.text = it.value
                    tvPaymentMethod.setTextColor(Color.parseColor("#2A2A2A"))
                }
            }
        }
        rlPlay.isEnabled = shipping != null && paymentMethodType != null
        btnPlay.isEnabled = rlPlay.isEnabled
    }
}