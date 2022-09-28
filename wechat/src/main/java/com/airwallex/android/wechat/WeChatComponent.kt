package com.airwallex.android.wechat

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
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

class WeChatComponent : ActionComponent {

    private var listener: Airwallex.PaymentResultListener? = null

    private var weChatApi: IWXAPI? = null

    private lateinit var paymentIntentId: String

    companion object {
        val PROVIDER: ActionComponentProvider<WeChatComponent> = WeChatComponentProvider()
    }

    internal fun handleIntent(intent: Intent, onEnd: () -> Unit) {
        weChatApi?.handleIntent(
            intent,
            object : IWXAPIEventHandler {
                override fun onReq(resp: BaseReq) {}

                override fun onResp(resp: BaseResp) {
                    when (resp.errCode) {
                        BaseResp.ErrCode.ERR_OK -> listener?.onCompleted(
                            AirwallexPaymentStatus.Success(
                                paymentIntentId
                            )
                        )
                        BaseResp.ErrCode.ERR_USER_CANCEL -> listener?.onCompleted(
                            AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "WeChat Pay has been cancelled!"))
                        )
                        else -> listener?.onCompleted(
                            AirwallexPaymentStatus.Failure(
                                AirwallexCheckoutException(message = "Failed to process WeChat Pay, errCode ${resp.errCode}, errStr ${resp.errStr}")
                            )
                        )
                    }
                    onEnd.invoke()
                }
            }
        )
    }

    override fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener
    ) {
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
                        listener.onCompleted(
                            AirwallexPaymentStatus.Failure(
                                AirwallexCheckoutException(message = "Failed to initialize WeChat app.")
                            )
                        )
                    } else {
                        listener.onCompleted(AirwallexPaymentStatus.InProgress(paymentIntentId))
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

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        // Since only card payments require a device ID, this will not be executed
        securityTokenListener.onResponse("")
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
