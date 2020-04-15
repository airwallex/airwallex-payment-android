package com.airwallex.paymentacceptance

import android.content.Context
import com.airwallex.android.model.WeChat
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

    private var listener: WeChatPaymentListener? = null

    fun launchWeChat(
        context: Context,
        appId: String,
        data: WeChat,
        listener: WeChatPaymentListener
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

    private fun launchWeChat(data: WeChat) {
        val success = weChatApi.registerApp(Settings.wechatAppSignature)
        assert(success)
        weChatApi.sendReq(createPayReq(data))
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
        return weChatReq
    }

    interface WeChatPaymentListener {
        fun onSuccess()

        fun onFailure(errCode: String?, errMessage: String?)

        fun onCancel()
    }
}
