package com.airwallex.android.core

import android.app.Activity
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction

interface ActionComponentProvider<Component : ActionComponent?> {

    fun get(): Component

    fun getType(): ActionComponentProviderType

    fun canHandleAction(nextAction: NextAction?): Boolean

    suspend fun canHandleSessionAndPaymentMethod(
        session: AirwallexSession,
        paymentMethodType: AvailablePaymentMethodType,
        activity: Activity
    ): Boolean = true
}
