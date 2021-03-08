package com.airwallex.android.model

/**
 * The params that used for disable [PaymentMethod]
 */
internal data class DisablePaymentMethodParams internal constructor(
    override val clientSecret: String,
    override val customerId: String,
    val paymentMethodId: String,
) : AbstractPaymentMethodParams(customerId = customerId, clientSecret = clientSecret)
