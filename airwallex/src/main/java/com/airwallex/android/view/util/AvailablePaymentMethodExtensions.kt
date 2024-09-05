package com.airwallex.android.view.util

import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType

fun List<AvailablePaymentMethodType>.findWithType(type: PaymentMethodType): AvailablePaymentMethodType? {
    return find { it.name == type.value }
}

fun List<AvailablePaymentMethodType>.hasSinglePaymentMethod(
    desiredPaymentMethodType: AvailablePaymentMethodType?,
    consents: List<PaymentConsent>
): Boolean {
    if (desiredPaymentMethodType == null) return false

    val hasPaymentConsents = consents.isNotEmpty()
    val availablePaymentMethodsSize = this.size

    return !hasPaymentConsents && availablePaymentMethodsSize == 1
}
