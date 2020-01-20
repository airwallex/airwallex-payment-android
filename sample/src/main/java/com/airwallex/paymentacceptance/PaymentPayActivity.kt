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
import com.airwallex.android.model.*
import com.neovisionaries.i18n.CountryCode
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
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

    private lateinit var weChatApi: IWXAPI

    companion object {

        private const val TAG = "PaymentPayActivity"

        private const val PAYMENT_INTENT_ID = "payment_intent_id"
        private const val PAYMENT_AMOUNT = "payment_amount"
        private const val PAYMENT_TOKEN = "payment_token"

        private const val REQUEST_EIDT_SHIPPING_CODE = 998
        private const val REQUEST_PAYMENT_MOTHOD_CODE = 999

        fun start(activity: Activity, paymentIntentId: String, amount: Float, token: String) {
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

        weChatApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true)

        rlPlay.setOnClickListener {
            loading.visibility = View.VISIBLE

            // Start Confirm PaymentIntent
            val airwallex = Airwallex(this, token)
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
                            .setType("card")
                            .setCard(
                                PaymentMethod.Card.Builder()
                                    .setNumber("4012000300001003")
                                    .setExpMonth("12")
                                    .setExpYear("2020")
                                    .setCvc("123")
                                    .setName("Adam")
                                    .build()
                            )
                            .setBilling(PaymentData.shipping)
                            .build()
                    )
                    .build(),
                callback = object : Airwallex.PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {

//                        launchWeChat(
//                            WeChat(
//                                appId = "wxfad13fd6681a62b0",
//                                partnerId = "334777613",
//                                prepayId = "wx2010563737889845f8f386d71754657400",
//                                packageValue = "Sign=WXPay",
//                                nonce = "h4di4JfuuQuiJIIo6kX4NvBWaASWwpoh",
//                                timestamp = "1579488997",
//                                sign = "198CF2019DF64D1822807A32E3F18F8D2062F10583BC2C2005B018D200ADFA3D"
//                            )
//                        )

                        airwallex.retrievePaymentIntent(
                            paymentIntentId = paymentIntentId,
                            callback = object : Airwallex.PaymentIntentCallback {
                                override fun onSuccess(paymentIntent: PaymentIntent) {
                                    loading.visibility = View.GONE

                                    Toast.makeText(
                                        this@PaymentPayActivity,
                                        "Payment Success!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()

                                    finish()
                                }

                                override fun onFailed() {
                                    loading.visibility = View.GONE
                                    Log.e(TAG, "Retrieve PaymentIntent failed")
                                }

                            })
                    }

                    override fun onFailed() {
                        loading.visibility = View.GONE
                        Log.e(TAG, "Confirm PaymentIntent failed")
                    }
                }
            )
        }

        val shipping = PaymentData.shipping
        if (shipping == null) {
            tvShipping.text = getString(R.string.select_shipping)
            tvShipping.setTextColor(Color.parseColor("#A9A9A9"))
        } else {
            updateShippingLabel(shipping)
        }

        if (PaymentData.paymentMethodType == null) {
            tvPaymentMethod.text = getString(R.string.select_payment_method)
            tvPaymentMethod.setTextColor(Color.parseColor("#A9A9A9"))
        } else {
            tvPaymentMethod.text = PaymentData.paymentMethodType!!.value
            tvPaymentMethod.setTextColor(Color.parseColor("#2A2A2A"))
        }

        rlPlay.isEnabled = shipping != null && PaymentData.paymentMethodType != null
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
                val shipping =
                    data.getParcelableExtra<Parcelable>(EditShippingActivity.SHIPPING_DETAIL) as PaymentMethod.Billing
                updateShippingLabel(shipping)
            }
            REQUEST_PAYMENT_MOTHOD_CODE -> {
                val paymentMethodType =
                    data.getSerializableExtra(PaymentSelectMethodActivity.PAYMENT_METHOD_TYPE) as PaymentMethodType
                tvPaymentMethod.text = paymentMethodType.value
                tvPaymentMethod.setTextColor(Color.parseColor("#2A2A2A"))
            }
        }
        rlPlay.isEnabled = PaymentData.shipping != null && PaymentData.paymentMethodType != null
        btnPlay.isEnabled = rlPlay.isEnabled
    }

    private fun launchWeChat(weChat: WeChat) {
        val success = weChatApi.registerApp(Constants.APP_SIGNATURE)
        assert(success)
        weChatApi.sendReq(createPayReq(weChat))
    }

    private fun createPayReq(weChat: WeChat): PayReq {
        val weChatReq = PayReq()
        weChatReq.appId = weChat.appId
        weChatReq.partnerId = weChat.partnerId
        weChatReq.prepayId = weChat.prepayId
        weChatReq.packageValue = weChat.packageValue
        weChatReq.nonceStr = weChat.nonce
        weChatReq.timeStamp = weChat.timestamp
        weChatReq.sign = weChat.sign

        return weChatReq
    }

    data class WeChat(
        val appId: String,
        val partnerId: String,
        val prepayId: String,
        val packageValue: String,
        val nonce: String,
        val timestamp: String,
        val sign: String
    )
}