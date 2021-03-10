package com.airwallex.android.model

/**
 * The params that used for create [PaymentMethod]
 */
internal data class CreatePaymentMethodParams internal constructor(
    override val clientSecret: String,
    override val customerId: String,
    /**
     * The card info of the [PaymentMethod]
     */
    val card: PaymentMethod.Card,
    /**
     * The billing info of the [PaymentMethod]
     */
    val billing: Billing?
) : AbstractPaymentMethodParams(customerId = customerId, clientSecret = clientSecret)
