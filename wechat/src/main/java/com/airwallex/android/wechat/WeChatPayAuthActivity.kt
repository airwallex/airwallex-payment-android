package com.airwallex.android.wechat

import android.app.Activity
import android.os.Bundle

internal class WeChatPayAuthActivity : Activity() {

    private val weChatComponent by lazy {
        (WeChatComponent.PROVIDER as WeChatComponentProvider).weChatComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            weChatComponent.handleIntent(
                intent = intent
            ) {
                finish()
            }
        }
    }
}
