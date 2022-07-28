package com.airwallex.android.googlepay

import com.airwallex.android.core.ActionComponentProvider
import com.airwallex.android.core.ActionComponentProviderType
import com.airwallex.android.core.model.NextAction

class GooglePayComponentProvider : ActionComponentProvider<GooglePayComponent> {
    val googlePayComponent: GooglePayComponent by lazy {
        GooglePayComponent()
    }

    override fun get(): GooglePayComponent {
        return googlePayComponent
    }

    override fun getType(): ActionComponentProviderType {
        return ActionComponentProviderType.GOOGLEPAY
    }

    override fun canHandleAction(nextAction: NextAction?): Boolean {
        TODO("Not yet implemented")
    }
}