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
     * Payment method type
     */
    val paymentMethodType: String,

    /**
     * Payment Method
     */
    val paymentMethod: PaymentMethod? = null,

    /**
     * CVC
     */
    val cvc: String? = null,

    /**
     * Indicate if the payment shall be captured immediately after authorized. Only applicable to Card.
     * Default: true
     */
    val autoCapture: Boolean = true,

    /**
     * Unique identifier of this [PaymentConsent]
     */
    val paymentConsentId: String? = null,

    /**
     * Currency
     */
    val currency: String? = null,

    /**
     * Country Code
     */
    val countryCode: String? = null,

    /**
     * AdditionalInfo (used by LPMs)
     */
    val additionalInfo: Map<String, String>? = null,

    /**
     * The URL to redirect your customer back to after they authenticate their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme.
     */
    val returnUrl: String? = null,

    /**
     * Payment Request Flow
     */
    val flow: AirwallexPaymentRequestFlow? = null

) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    class Builder(
        private val paymentIntentId: String,
        private val clientSecret: String,
        private var paymentMethodType: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var paymentMethod: PaymentMethod? = null
        private var cvc: String? = null
        private var autoCapture: Boolean = true
        private var customerId: String? = null
        private var paymentConsentId: String? = null
        private var currency: String? = null
        private var countryCode: String? = null
        private var additionalInfo: Map<String, String>? = null
        private var returnUrl: String? = null
        private var flow: AirwallexPaymentRequestFlow? = null

        fun setCVC(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        fun setAutoCapture(autoCapture: Boolean): Builder = apply {
            this.autoCapture = autoCapture
        }

        fun setAdditionalInfo(additionalInfo: Map<String, String>?): Builder = apply {
            this.additionalInfo = additionalInfo
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

        fun setCountryCode(countryCode: String?): Builder = apply {
            this.countryCode = countryCode
        }

        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
            this.paymentMethod = paymentMethod
        }

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        fun setFlow(flow: AirwallexPaymentRequestFlow?): Builder = apply {
            this.flow = flow
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId,
                paymentMethodType = paymentMethodType,
                paymentMethod = paymentMethod,
                cvc = cvc,
                autoCapture = autoCapture,
                paymentConsentId = paymentConsentId,
                currency = currency,
                countryCode = countryCode,
                additionalInfo = additionalInfo,
                returnUrl = returnUrl,
                flow = flow
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
         * @param additionalInfo used by LPMs
         * @param returnUrl optional
         * @param flow optional
         */
        fun createThirdPartPayParams(
            paymentMethodType: String,
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null,
            currency: String? = null,
            countryCode: String? = null,
            additionalInfo: Map<String, String>? = null,
            returnUrl: String? = null,
            flow: AirwallexPaymentRequestFlow? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                paymentMethodType = paymentMethodType
            )
                .setCustomerId(customerId)
                .setPaymentConsentId(paymentConsentId)
                .setCurrency(currency)
                .setCountryCode(countryCode)
                .setAdditionalInfo(additionalInfo)
                .setReturnUrl(returnUrl)
                .setFlow(flow)
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
        @Suppress("LongParameterList")
        fun createCardParams(
            paymentIntentId: String,
            clientSecret: String,
            paymentMethod: PaymentMethod? = null,
            cvc: String? = null,
            customerId: String? = null,
            paymentConsentId: String? = null,
            returnUrl: String? = null,
            autoCapture: Boolean = true
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                paymentMethodType = PaymentMethodType.CARD.value
            )
                .setCustomerId(customerId)
                .setPaymentMethod(paymentMethod)
                .setCVC(cvc)
                .setAutoCapture(autoCapture)
                .setPaymentConsentId(paymentConsentId)
                .setReturnUrl(returnUrl)
                .build()
        }
    }
}
