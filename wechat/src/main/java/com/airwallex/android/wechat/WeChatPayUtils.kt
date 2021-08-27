package com.airwallex.android.wechat

import android.app.Application
import android.content.Intent
import com.airwallex.android.core.model.WeChat
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory

object WeChatPayUtils {

    private const val RESULT_EXTRA_KEY = "_wxapi_baseresp_errstr"

    fun isResultIntent(intent: Intent?): Boolean {
        return intent != null && intent.extras != null && intent.extras!!.containsKey(
            RESULT_EXTRA_KEY
        )
    }

    fun isAvailable(applicationContext: Application?): Boolean {
        val api = WXAPIFactory.createWXAPI(applicationContext, null, true)
        val isAppInstalled = api.isWXAppInstalled
        val isSupported = Build.PAY_SUPPORTED_SDK_INT <= api.wxAppSupportAPI
        api.detach()
        return isAppInstalled && isSupported
    }

    fun createPayReq(weChat: WeChat): PayReq {
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
}
