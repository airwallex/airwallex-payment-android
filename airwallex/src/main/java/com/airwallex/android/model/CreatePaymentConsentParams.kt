package com.airwallex.android.model

/**
 * The params that used for create [PaymentConsent]
 */
data class CreatePaymentConsentParams constructor(
    val clientSecret: String,
    val customerId: String,

    /**
     * ID of the PaymentMethod attached for subsequent payments. Must be set when type is card.
     */
    val paymentMethodId: String? = null,

    /**
     * Type of the PaymentMethod. One of card, alipayhk, kakaopay, gcash, dana, tng
     */
    val paymentMethodType: PaymentMethodType,

    /**
     * The party to trigger subsequent payments. Can be one of merchant, customer. If type of payment_method is card, both merchant and customer is supported. Otherwise, only merchant is supported
     */
    val nextTriggeredBy: PaymentConsent.NextTriggeredBy,

    /**
     * Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled.
     * Default: unscheduled
     */
    val merchantTriggerReason: PaymentConsent.MerchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,

    /**
     * Only applicable when next_triggered_by is customer. If false, the customer must provide cvc for subsequent payments with this PaymentConsent.
     * Default: false
     */
    val requiresCvc: Boolean = false
)
