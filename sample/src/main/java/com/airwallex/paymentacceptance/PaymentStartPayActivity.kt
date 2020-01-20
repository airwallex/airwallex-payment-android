package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.model.Shipping
import com.neovisionaries.i18n.CountryCode
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.activity_start_pay.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentStartPayActivity : AppCompatActivity() {

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

        private const val PAYMENT_INTENT_ID = "payment_intent_id"
        private const val PAYMENT_AMOUNT = "payment_amount"
        private const val PAYMENT_TOKEN = "payment_token"

        private const val REQUEST_EIDT_SHIPPING_CODE = 998
        private const val REQUEST_PAYMENT_MOTHOD_CODE = 999

        fun start(activity: Activity, paymentIntentId: String, amount: Float, token: String) {
            val intent = Intent(activity, PaymentStartPayActivity::class.java)
            intent.putExtra(PAYMENT_INTENT_ID, paymentIntentId)
            intent.putExtra(PAYMENT_AMOUNT, amount)
            intent.putExtra(PAYMENT_TOKEN, token)
            activity.startActivity(intent)
        }
    }

    private lateinit var weChatApi: IWXAPI

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
//            val airwallex = Airwallex(this, token)
//            airwallex.confirmPaymentIntent(paymentIntentId)


            CoroutineScope(Dispatchers.IO).launch {


//                airwallex.retrievePaymentIntent("int_jjLlyQTiz1h49tZkZzgJDDEHABC")


            }

            launchWeChat(
                WeChat(
                    appId = "wxfad13fd6681a62b0",
                    partnerId = "334777613",
                    prepayId = "wx191803162627511bc2299b671801586900",
                    packageValue = "Sign=WXPay",
                    nonce = "RbUilMJ9vKfPMJxDXpk51GOcdz1NMmB8",
                    timestamp = "1579428196",
                    sign = "AF83A58CAB542EE60EC652533E6AA43A505AB62B4C3C2C461A1A28CEB1FC4D1C"
                )
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

    private fun updateShippingLabel(shipping: Shipping) {
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
                    data.getParcelableExtra<Parcelable>(EditShippingActivity.SHIPPING_DETAIL) as Shipping
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