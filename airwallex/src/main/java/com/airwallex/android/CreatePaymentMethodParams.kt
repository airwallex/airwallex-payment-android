package com.airwallex.android

import com.airwallex.android.model.Billing
import com.airwallex.android.model.PaymentMethod

data class CreatePaymentMethodParams internal constructor(
    override val clientSecret: String,
    override val customerId: String,
    val card: PaymentMethod.Card,
    val billing: Billing
) : AbstractPaymentMethodParams(customerId = customerId, clientSecret = clientSecret)
