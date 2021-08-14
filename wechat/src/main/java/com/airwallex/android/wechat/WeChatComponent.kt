package com.airwallex.android.wechat

import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.ActionComponentProvider

class WeChatComponent : ActionComponent {

    companion object {
        val PROVIDER: ActionComponentProvider<WeChatComponent> = WeChatComponentProvider()
    }
}
