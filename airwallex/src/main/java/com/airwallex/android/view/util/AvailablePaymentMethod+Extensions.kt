package com.airwallex.android.view.util

import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType

fun List<AvailablePaymentMethodType>.findWithType(type: PaymentMethodType): AvailablePaymentMethodType? {
    return find { it.name == type.value }
}

fun List<AvailablePaymentMethodType>.notOnlyCard(): Boolean {
    return this.size != 1 || this.first().name != PaymentMethodType.CARD.value
}