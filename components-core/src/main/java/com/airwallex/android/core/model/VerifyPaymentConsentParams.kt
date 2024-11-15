package com.airwallex.android.core.model

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
     * Type of the PaymentMethod.
     */
    val paymentMethodType: String,

    /**
     * The URL to which your customer will be redirected after they verify PaymentConsent on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively provide an application URI scheme.
     */
    val returnUrl: String? = null
) {
    class Builder(
        private val clientSecret: String,
        private val paymentConsentId: String,
        private val paymentMethodType: String
    ) : ObjectBuilder<VerifyPaymentConsentParams> {

        private var amount: BigDecimal? = null
        private var currency: String? = null
        private var cvc: String? = null
        private var returnUrl: String? = null

        fun setAmount(amount: BigDecimal?): Builder = apply {
            this.amount = amount
        }

        fun setCurrency(currency: String?): Builder = apply {
            this.currency = currency
        }

        fun setCvc(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        override fun build(): VerifyPaymentConsentParams {
            return VerifyPaymentConsentParams(
                clientSecret = clientSecret,
                paymentConsentId = paymentConsentId,
                amount = amount,
                currency = currency,
                cvc = cvc,
                paymentMethodType = paymentMethodType,
                returnUrl = returnUrl
            )
        }
    }

    companion object {

        /**
         * Return the [CreatePaymentConsentParams] for Card
         */
        fun createCardParams(
            clientSecret: String,
            paymentConsentId: String,
            amount: BigDecimal?,
            currency: String?,
            cvc: String?,
            returnUrl: String?
        ): VerifyPaymentConsentParams {
            return Builder(
                clientSecret = clientSecret,
                paymentConsentId = paymentConsentId,
                paymentMethodType = PaymentMethodType.CARD.value
            )
                .setAmount(amount = amount)
                .setCurrency(currency = currency)
                .setCvc(cvc)
                .setReturnUrl(returnUrl)
                .build()
        }

        /**
         * Return the [CreatePaymentConsentParams] for Google Pay
         */
        fun createGoogleParams(
            clientSecret: String,
            paymentConsentId: String,
            amount: BigDecimal?,
            currency: String?,
            cvc: String?,
            returnUrl: String?
        ): VerifyPaymentConsentParams {
            return Builder(
                clientSecret = clientSecret,
                paymentConsentId = paymentConsentId,
                paymentMethodType = PaymentMethodType.GOOGLEPAY.value
            )
                .setAmount(amount = amount)
                .setCurrency(currency = currency)
                .setCvc(cvc)
                .setReturnUrl(returnUrl)
                .build()
        }

        fun createThirdPartParams(
            paymentMethodType: String,
            clientSecret: String,
            paymentConsentId: String,
            returnUrl: String?
        ): VerifyPaymentConsentParams {
            return Builder(
                clientSecret = clientSecret,
                paymentConsentId = paymentConsentId,
                paymentMethodType = paymentMethodType,
            )
                .setReturnUrl(returnUrl)
                .build()
        }
    }
}
