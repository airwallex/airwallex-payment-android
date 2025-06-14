package com.airwallex.android.wechat

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.WeChat
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("LongMethod")
class WeChatComponent : ActionComponent {

    private var listener: Airwallex.PaymentResultListener? = null

    private var weChatApi: IWXAPI? = null

    private lateinit var paymentIntentId: String

    companion object {
        val PROVIDER: ActionComponentProvider<WeChatComponent> = WeChatComponentProvider()
        private const val EVENT_NAME = "wechat_redirect"
    }

    internal fun handleIntent(
        intent: Intent,
        onCompletion: () -> Unit
    ) {
        weChatApi?.handleIntent(
            intent,
            object : IWXAPIEventHandler {
                override fun onReq(resp: BaseReq) {}

                override fun onResp(resp: BaseResp) {
                    val status = when (resp.errCode) {
                        BaseResp.ErrCode.ERR_OK ->
                            AirwallexPaymentStatus.Success(paymentIntentId)
                        BaseResp.ErrCode.ERR_USER_CANCEL ->
                            AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "WeChat Pay has been cancelled!"))
                        else -> {
                            val exception =
                                AirwallexCheckoutException(message = "Failed to process WeChat Pay, errCode ${resp.errCode}, errStr ${resp.errStr}")
                            AnalyticsLogger.logError(EVENT_NAME, exception = exception)
                            AirwallexPaymentStatus.Failure(exception)
                        }
                    }
                    listener?.onCompleted(status)
                    listener = null
                    onCompletion()
                }
            }
        )
    }

    override fun handlePaymentIntentResponse(
        paymentIntentId: String?,
        nextAction: NextAction?,
        fragment: Fragment?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener,
        consentId: String?
    ) {
        if (paymentIntentId == null) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "paymentIntentId is null")
                )
            )
            return
        }
        this.paymentIntentId = paymentIntentId
        this.listener = listener
        when (nextAction?.type) {
            NextAction.NextActionType.CALL_SDK -> {
                val weChat = nextAction.getWechatData()
                if (weChat == null) {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(
                                message = "WeChatPay Data not found."
                            )
                        )
                    )
                    return
                }

                val prepayId = weChat.prepayId

                if (prepayId?.startsWith("http") == true) {
                    // We use the `URL mock` method to simulate WeChat Pay in the `Staging` environment.
                    // By requesting this URL, we will set the status of the `PaymentIntent` to success.
                    val apiRepository = AirwallexApiRepository()
                    CoroutineScope(Dispatchers.IO).launch {
                        apiRepository.executeMockWeChat(prepayId)
                        withContext(Dispatchers.Main) {
                            listener.onCompleted(AirwallexPaymentStatus.Success(paymentIntentId))
                        }
                    }
                } else {
                    if (weChatApi == null) {
                        weChatApi = WXAPIFactory.createWXAPI(applicationContext, null, true)
                    }
                    val weChatInitiated = initiateWeChatPay(weChat)
                    if (!weChatInitiated) {
                        val errorMsg = "Failed to initialize WeChat app."
                        listener.onCompleted(
                            AirwallexPaymentStatus.Failure(
                                AirwallexCheckoutException(message = errorMsg)
                            )
                        )
                        AnalyticsLogger.logError(
                            EVENT_NAME,
                            mapOf("message" to errorMsg)
                        )
                    } else {
                        PaymentResultManager.getInstance().updateStatus(AirwallexPaymentStatus.InProgress(paymentIntentId))
                        AnalyticsLogger.logPageView(EVENT_NAME)
                    }
                }
            }
            else -> {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(
                            message = "Unsupported next action ${nextAction?.type}"
                        )
                    )
                )
            }
        }
    }

    override fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        listener: Airwallex.PaymentResultListener?
    ): Boolean {
        return false
    }

    private fun initiateWeChatPay(
        weChatPaySdkData: WeChat
    ): Boolean {
        weChatApi?.registerApp(weChatPaySdkData.appId)
        return weChatApi?.sendReq(createPayReq(weChatPaySdkData)) ?: false
    }

    private fun createPayReq(weChat: WeChat): PayReq {
        val weChatReq = PayReq()
        weChatReq.appId = weChat.appId
        weChatReq.partnerId = weChat.partnerId
        weChatReq.prepayId = weChat.prepayId
        weChatReq.packageValue = weChat.`package`
        weChatReq.nonceStr = weChat.nonceStr
        weChatReq.timeStamp = weChat.timestamp
        weChatReq.sign = weChat.sign

        weChatReq.options = PayReq.Options()
        weChatReq.options.callbackClassName = WeChatPayAuthActivity::class.java.name

        return weChatReq
    }

    private fun NextAction.getWechatData(): WeChat? {
        return data?.let {
            WeChat(
                appId = it["appId"] as? String,
                partnerId = it["partnerId"] as? String,
                prepayId = it["prepayId"] as? String,
                `package` = it["package"] as? String,
                nonceStr = it["nonceStr"] as? String,
                timestamp = it["timeStamp"] as? String,
                sign = it["sign"] as? String
            )
        }
    }
}
