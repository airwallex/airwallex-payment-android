package com.airwallex.android.core.model

/**
 * The params that used for create [PaymentMethod]
 */
data class CreatePaymentMethodParams(
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
