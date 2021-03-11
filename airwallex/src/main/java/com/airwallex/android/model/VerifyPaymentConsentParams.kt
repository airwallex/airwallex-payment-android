package com.airwallex.android.model

import java.math.BigDecimal

/**
 * The params that used for verify [PaymentConsent]
 */
data class VerifyPaymentConsentParams constructor(
    val clientSecret: String,

    /**
     * PaymentConsent ID
     */
    val paymentConsentId: String,

    /**
     * The alternative amount of verification if zero amount is not acceptable for the provider. The transaction of this amount should be reverted once the verification process finished. Must be greater than 0.
     */
    val amount: BigDecimal? = null,

    /**
     * Currency of the initial PaymentIntent to verify the PaymentConsent. Three-letter ISO currency code. Must be a supported currency
     */
    val currency: String? = null,

    /**
     * When requires_cvc for the PaymentConsent is true, this attribute must be provided in order to confirm successfully
     */
    val cvc: String? = null,

    /**
     * Type of the PaymentMethod. One of card, alipayhk, kakaopay, gcash, dana, tng
     */
    val paymentMethodType: PaymentMethodType? = null,

    /**
     * The URL to which your customer will be redirected after they verify PaymentConsent on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively provide an application URI scheme.
     */
    val returnUrl: String?
)
