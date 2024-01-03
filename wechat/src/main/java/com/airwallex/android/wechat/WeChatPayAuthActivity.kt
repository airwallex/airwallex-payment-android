package com.airwallex.android.wechat

import android.app.Activity
import android.os.Bundle
import com.airwallex.android.core.PaymentResultManager

internal class WeChatPayAuthActivity : Activity() {

    private val weChatComponent by lazy {
        (WeChatComponent.PROVIDER as WeChatComponentProvider).weChatComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            weChatComponent.handleIntent(
                intent = intent
            ) { status ->
                PaymentResultManager.getInstance().completePayment(status)
                finish()
            }
        }
    }
}
