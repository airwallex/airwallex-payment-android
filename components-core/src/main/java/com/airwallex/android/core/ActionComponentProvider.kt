package com.airwallex.android.core

import com.airwallex.android.core.model.NextAction

interface ActionComponentProvider<Component : ActionComponent?> {

    fun get(): Component

    fun getType(): ActionComponentProviderType

    fun canHandleAction(nextAction: NextAction?): Boolean
}
