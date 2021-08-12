package com.airwallex.android.core.model

/**
 * The params that used for confirm [PaymentIntent]
 */
data class ConfirmPaymentIntentParams internal constructor(
    override val paymentIntentId: String,
    override val clientSecret: String,
    /**
     * optional, the ID of a Customer.
     */
    val customerId: String?,

    /**
     * Payment method type, default is [PaymentMethodType.WECHAT]
     */
    val paymentMethodType: PaymentMethodType = PaymentMethodType.WECHAT,

    /**
     * Payment Method
     */
    val paymentMethod: PaymentMethod? = null,

    /**
     * CVC
     */
    val cvc: String? = null,

    /**
     * Unique identifier of this [PaymentConsent]
     */
    val paymentConsentId: String? = null,

    /**
     * Currency
     */
    val currency: String? = null,

    /**
     * PPROAdditionalInfo
     */
    val pproAdditionalInfo: PPROAdditionalInfo? = null,

    /**
     * The URL to redirect your customer back to after they authenticate their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme.
     */
    val returnUrl: String? = null

) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    class Builder(
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var paymentMethodType: PaymentMethodType = PaymentMethodType.WECHAT
        private var paymentMethod: PaymentMethod? = null
        private var cvc: String? = null
        private var customerId: String? = null
        private var paymentConsentId: String? = null
        private var currency: String? = null
        private var pproAdditionalInfo: PPROAdditionalInfo? = null
        private var returnUrl: String? = null

        fun setCVC(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        fun setPPROAdditionalInfo(pproAdditionalInfo: PPROAdditionalInfo?): Builder = apply {
            this.pproAdditionalInfo = pproAdditionalInfo
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentConsentId(paymentConsentId: String?): Builder = apply {
            this.paymentConsentId = paymentConsentId
        }

        fun setCurrency(currency: String?): Builder = apply {
            this.currency = currency
        }

        fun setPaymentMethod(
            paymentMethodType: PaymentMethodType,
            paymentMethod: PaymentMethod? = null
        ): Builder = apply {
            this.paymentMethodType = paymentMethodType
            this.paymentMethod = paymentMethod
        }

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId,
                paymentMethodType = paymentMethodType,
                paymentMethod = paymentMethod,
                cvc = cvc,
                paymentConsentId = paymentConsentId,
                currency = currency,
                pproAdditionalInfo = pproAdditionalInfo,
                returnUrl = returnUrl
            )
        }
    }

    companion object {

        /**
         * Return the [ConfirmPaymentIntentParams] for ThirdPart Pay
         *
         * @param paymentMethodType Payment method type, required.
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId the customerId of [PaymentIntent], optional.
         * @param paymentConsentId the customerId of [PaymentConsent], optional.
         * @param currency amount currency
         * @param pproAdditionalInfo to support ppro payment
         * @param returnUrl optional
         */
        fun createThirdPartPayParams(
            paymentMethodType: PaymentMethodType,
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null,
            currency: String? = null,
            pproAdditionalInfo: PPROAdditionalInfo? = null,
            returnUrl: String? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret
            )
                .setCustomerId(customerId)
                .setPaymentMethod(paymentMethodType)
                .setPaymentConsentId(paymentConsentId)
                .setCurrency(currency)
                .setPPROAdditionalInfo(pproAdditionalInfo)
                .setReturnUrl(returnUrl)
                .build()
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for Credit Card Pay
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param paymentMethod the object of the [PaymentMethod], required.
         * @param cvc optional.
         * @param customerId the customerId of [PaymentIntent], optional.
         * @param paymentConsentId the customerId of [PaymentConsent], optional.
         * @param returnUrl optional
         */
        fun createCardParams(
            paymentIntentId: String,
            clientSecret: String,
            paymentMethod: PaymentMethod,
            cvc: String?,
            customerId: String? = null,
            paymentConsentId: String? = null,
            returnUrl: String? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret
            )
                .setCustomerId(customerId)
                .setPaymentMethod(PaymentMethodType.CARD, paymentMethod)
                .setCVC(cvc)
                .setPaymentConsentId(paymentConsentId)
                .setReturnUrl(returnUrl)
                .build()
        }
    }
}
