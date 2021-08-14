package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentMethodType

interface ActionComponentProvider<ComponentT : ActionComponent?> :
    ComponentProvider<ComponentT> {

    fun canHandleAction(paymentMethodType: PaymentMethodType): Boolean
}
