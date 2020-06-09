package com.airwallex.paymentacceptance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class AppRegister : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val api = WXAPIFactory.createWXAPI(
            context,
            Settings.weChatAppId, true
        )
        api.registerApp(Settings.weChatAppSignature)
    }
}
