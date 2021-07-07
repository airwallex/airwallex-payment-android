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
    val phone: String? = null,

    val bank: Bank? = null

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
        private var name: String? = null
        private var email: String? = null
        private var phone: String? = null
        private var bank: Bank? = null

        fun setCVC(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        fun setName(name: String?): Builder = apply {
            this.name = name
        }

        fun setEmail(email: String?): Builder = apply {
            this.email = email
        }

        fun setPhone(phone: String?): Builder = apply {
            this.phone = phone
        }

        fun setBank(bank: Bank?): Builder = apply {
            this.bank = bank
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
                name = name,
                email = email,
                phone = phone,
                bank = bank
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
         * @param currency
         * @param name
         * @param email
         * @param phone
         * @param bank
         */
        fun createThirdPartPayParams(
            paymentMethodType: PaymentMethodType,
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null,
            currency: String? = null,
            name: String? = null,
            email: String? = null,
            phone: String? = null,
            bank: Bank? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret
            )
                .setCustomerId(customerId)
                .setPaymentMethod(paymentMethodType)
                .setPaymentConsentId(paymentConsentId)
                .setCurrency(currency)
                .setName(name)
                .setEmail(email)
                .setPhone(phone)
                .setBank(bank)
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
         */
        fun createCardParams(
            paymentIntentId: String,
            clientSecret: String,
            paymentMethod: PaymentMethod,
            cvc: String?,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret
            )
                .setCustomerId(customerId)
                .setPaymentMethod(PaymentMethodType.CARD, paymentMethod)
                .setCVC(cvc)
                .setPaymentConsentId(paymentConsentId)
                .build()
        }
    }
}
