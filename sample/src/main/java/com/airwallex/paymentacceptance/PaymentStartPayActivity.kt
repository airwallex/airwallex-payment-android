package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.Airwallex
import com.airwallex.paymentacceptance.wechat.Constants
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.activity_payment_methods.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentStartPayActivity : AppCompatActivity() {

    private val paymentIntentId: String by lazy {
        intent.getStringExtra(PAYMENT_INTENT_ID)
    }

    private val token: String by lazy {
        intent.getStringExtra(PAYMENT_TOKEN)
    }

    companion object {

        private const val PAYMENT_INTENT_ID = "payment_intent_id"
        private const val PAYMENT_TOKEN = "payment_token"

        fun start(activity: Activity, paymentIntentId: String, token: String) {
            val intent = Intent(activity, PaymentStartPayActivity::class.java)
            intent.putExtra(PAYMENT_INTENT_ID, paymentIntentId)
            intent.putExtra(PAYMENT_TOKEN, token)
            activity.startActivity(intent)
        }
    }

    private lateinit var weChatApi: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)


        weChatApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true)

//        val stripe = Stripe(this, Constants.STRIPE_KEY)
//        val weChatPaySourceParams = SourceParams.createWeChatPayParams(
//            MainActivity.AMOUNT,
//            MainActivity.CURRENCY,
//            Constants.APP_ID,
//            MainActivity.STATEMENT_DESCRIPTOR
//        )

        btnPay.setOnClickListener {

            //            stripe.createSource(weChatPaySourceParams,
//                object : ApiResultCallback<Source> {
//                    override fun onError(e: Exception) {
//                    }
//
//                    override fun onSuccess(source: Source) {
//
//                    }
//                })

//            launchWeChat(
//                WeChat(
//                    "",
//                    "",
//                    "",
//                    "",
//                    "",
//                    "",
//                    ""
//                )
//            )


            val airwallex = Airwallex(this, token)
//            airwallex.confirmPaymentIntent(paymentIntentId)


            CoroutineScope(Dispatchers.IO).launch {
                airwallex.retrievePaymentIntent("int_jjLlyQTiz1h49tZkZzgJDDEHABC")
            }
        }
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