package com.airwallex.paymentacceptance

import android.content.Context
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

    private var listener: PayListener? = null

    fun launchWeChat(context: Context, appId: String, listener: PayListener) {
        this.listener = listener

        if (!::weChatApi.isInitialized) {
            initWXApi(context, appId)
        }

        launchWeChat(
            WeChat(
                appId = "wxfad13fd6681a62b0",
                partnerId = "334777613",
                prepayId = "wx2010563737889845f8f386d71754657400",
                packageValue = "Sign=WXPay",
                nonce = "h4di4JfuuQuiJIIo6kX4NvBWaASWwpoh",
                timestamp = "1579488997",
                sign = "198CF2019DF64D1822807A32E3F18F8D2062F10583BC2C2005B018D200ADFA3D"
            )
        )
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