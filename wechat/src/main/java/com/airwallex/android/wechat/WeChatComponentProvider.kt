package com.airwallex.android.wechat

import com.airwallex.android.core.*
import com.airwallex.android.core.model.*

class WeChatComponentProvider : ActionComponentProvider<WeChatComponent> {

    val weChatComponent: WeChatComponent by lazy {
        WeChatComponent()
    }

    override fun canHandleAction(nextAction: NextAction?): Boolean {
        return nextAction?.type == NextAction.NextActionType.CALL_SDK
    }

    override fun get(): WeChatComponent {
        return weChatComponent
    }

    override fun getType(): ActionComponentProviderType {
        return ActionComponentProviderType.WECHAT
    }
}
