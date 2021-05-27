package com.airwallex.paymentacceptance

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

    private val weChatApi: IWXAPI by lazy {
        WXAPIFactory.createWXAPI(SampleApplication.instance, null)
    }

    private var listener: WeChatPaymentListener? = null

    fun launchWeChat(
        data: WeChat,
        listener: WeChatPaymentListener
    ) {
        this.listener = listener
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

    private fun launchWeChat(data: WeChat) {
        val success = weChatApi.registerApp(Settings.weChatAppId)
        if (success) {
            weChatApi.sendReq(createPayReq(data))
        } else {
            listener?.onFailure("0", "Failed to start WeChat Pay")
        }
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
