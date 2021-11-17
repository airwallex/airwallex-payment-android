package com.airwallex.android.redirect

import com.airwallex.android.core.*
import com.airwallex.android.core.model.*

class RedirectComponentProvider : ActionComponentProvider<RedirectComponent> {

    private val redirectComponent: RedirectComponent by lazy {
        RedirectComponent()
    }

    override fun canHandleAction(nextAction: NextAction?): Boolean {
        return nextAction?.type == NextAction.NextActionType.REDIRECT && nextAction.data == null
    }

    override fun get(): RedirectComponent {
        return redirectComponent
    }

    override fun getType(): ActionComponentProviderType {
        return ActionComponentProviderType.REDIRECT
    }
}
