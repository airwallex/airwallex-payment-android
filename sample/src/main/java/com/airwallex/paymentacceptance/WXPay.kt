package com.airwallex.paymentacceptance

import android.content.Context
import com.airwallex.android.model.PaymentIntent
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class WXPay {

    companion object {
        val instance: WXPay by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            WXPay()
        }
    }

    private lateinit var weChatApi: IWXAPI

    private var listener: WechatPaymentListener? = null

    fun launchWeChat(
        context: Context,
        appId: String,
        data: Map<String, Any>,
        listener: WechatPaymentListener
    ) {
        this.listener = listener

        if (!::weChatApi.isInitialized) {
            initWXApi(context, appId)
        }

        launchWeChat(data)
    }

    fun onResp(errCode: Int, errText: String?) {
        when (errCode) {
            BaseResp.ErrCode.ERR_OK -> listener?.onSuccess()
            BaseResp.ErrCode.ERR_COMM -> listener?.onFailure(errCode.toString(), errText)
            BaseResp.ErrCode.ERR_USER_CANCEL -> listener?.onCancel()
            else -> listener?.onFailure(errCode.toString(), errText)
        }
    }

    private fun initWXApi(context: Context, appId: String) {
        weChatApi = WXAPIFactory.createWXAPI(context.applicationContext, appId)
        weChatApi.registerApp(appId)
    }

    private fun launchWeChat(data: Map<String, Any>) {
        val success = weChatApi.registerApp(Settings.wechatAppSignature)
        assert(success)
        weChatApi.sendReq(createPayReq(data))
    }

    private fun createPayReq(weChat: Map<String, Any>): PayReq {
        val weChatReq = PayReq()
        weChatReq.appId = Settings.wechatAppId
        weChatReq.partnerId = weChat["partnerId"] as String
        weChatReq.prepayId = weChat["prepayId"] as String
        weChatReq.packageValue = weChat["package"] as String
        weChatReq.nonceStr = weChat["nonceStr"] as String
        weChatReq.timeStamp = weChat["timeStamp"] as String
        weChatReq.sign = weChat["sign"] as String

        return weChatReq
    }

    interface WechatPaymentListener {
        fun onSuccess()

        fun onFailure(errCode: String?, errMessage: String?)

        fun onCancel()
    }
}
