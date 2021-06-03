package com.airwallex.android.model

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
     * [PaymentMethodReference] used to confirm [PaymentIntent].
     * When [paymentMethodType] is [PaymentMethodType.CARD], it's should be not null
     * When [paymentMethodType] is [PaymentMethodType.WECHAT], it's should be null
     */
    val paymentMethodReference: PaymentMethodReference? = null,

    /**
     * Payment method type, default is [PaymentMethodType.WECHAT]
     */
    val paymentMethodType: PaymentMethodType = PaymentMethodType.WECHAT,

    /**
     * Unique identifier of this [PaymentConsent]
     */
    val paymentConsentId: String? = null,

    /**
     * Name, required for POLi & FPX
     */
    val name: String? = null,

    /**
     * Email, required for FPX
     */
    val email: String? = null,

    /**
     * Phone, required for FPX
     */
    val phone: String? = null

) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    class Builder(
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var paymentMethodType: PaymentMethodType = PaymentMethodType.WECHAT
        private var customerId: String? = null
        private var paymentMethodReference: PaymentMethodReference? = null
        private var paymentConsentId: String? = null
        private var name: String? = null
        private var email: String? = null
        private var phone: String? = null

        fun setName(name: String?): Builder = apply {
            this.name = name
        }

        fun setEmail(email: String?): Builder = apply {
            this.email = email
        }

        fun setPhone(phone: String?): Builder = apply {
            this.phone = phone
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentConsentId(paymentConsentId: String?): Builder = apply {
            this.paymentConsentId = paymentConsentId
        }

        fun setPaymentMethod(
            paymentMethodType: PaymentMethodType,
            paymentMethodReference: PaymentMethodReference? = null
        ): Builder = apply {
            this.paymentMethodType = paymentMethodType
            if (paymentMethodType == PaymentMethodType.CARD) {
                this.paymentMethodReference = requireNotNull(paymentMethodReference)
            }
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId,
                paymentMethodReference = paymentMethodReference,
                paymentMethodType = paymentMethodType,
                paymentConsentId = paymentConsentId,
                name = name,
                email = email,
                phone = phone
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
         */
        fun createThirdPartPayParams(
            paymentMethodType: PaymentMethodType,
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null,
            name: String? = null,
            email: String? = null,
            phone: String? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret
            )
                .setCustomerId(customerId)
                .setPaymentMethod(paymentMethodType)
                .setPaymentConsentId(paymentConsentId)
                .setName(name)
                .setEmail(email)
                .setPhone(phone)
                .build()
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for Credit Card Pay
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param paymentMethodId the ID of the [PaymentMethod], required.
         * @param cvc the CVC of the Credit Card, required.
         * @param customerId the customerId of [PaymentIntent], optional.
         */
        fun createCardParams(
            paymentIntentId: String,
            clientSecret: String,
            paymentMethodId: String,
            cvc: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret
            )
                .setCustomerId(customerId)
                .setPaymentMethod(
                    PaymentMethodType.CARD,
                    PaymentMethodReference(paymentMethodId, cvc)
                )
                .setPaymentConsentId(paymentConsentId)
                .build()
        }
    }
}
