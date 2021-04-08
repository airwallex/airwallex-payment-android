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
     * When [paymentMethodType] is [AvaliablePaymentMethodType.CARD], it's should be not null
     * When [paymentMethodType] is [AvaliablePaymentMethodType.WECHAT], it's should be null
     */
    val paymentMethodReference: PaymentMethodReference? = null,

    /**
     * Payment method type, default is [AvaliablePaymentMethodType.WECHAT]
     */
    val paymentMethodType: AvaliablePaymentMethodType = AvaliablePaymentMethodType.WECHAT,

    /**
     * Unique identifier of this [PaymentConsent]
     */
    val paymentConsentId: String? = null
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    class Builder(
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var paymentMethodType: AvaliablePaymentMethodType = AvaliablePaymentMethodType.WECHAT
        private var customerId: String? = null
        private var paymentMethodReference: PaymentMethodReference? = null
        private var paymentConsentId: String? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentConsentId(paymentConsentId: String?): Builder = apply {
            this.paymentConsentId = paymentConsentId
        }

        fun setPaymentMethod(
            paymentMethodType: AvaliablePaymentMethodType,
            paymentMethodReference: PaymentMethodReference? = null
        ): Builder = apply {
            this.paymentMethodType = paymentMethodType
            if (paymentMethodType == AvaliablePaymentMethodType.CARD) {
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
                paymentConsentId = paymentConsentId
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
        private fun createThirdPartPayParams(
            paymentMethodType: AvaliablePaymentMethodType,
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret
            )
                .setCustomerId(customerId)
                .setPaymentMethod(paymentMethodType)
                .setPaymentConsentId(paymentConsentId)
                .build()
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for WeChat Pay
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId the customerId of [PaymentIntent], optional.
         */
        fun createWeChatParams(
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return createThirdPartPayParams(AvaliablePaymentMethodType.WECHAT, paymentIntentId, clientSecret, customerId, paymentConsentId)
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for AliPay CN
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId the customerId of [PaymentIntent], optional.
         */
        fun createAlipayParams(
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return createThirdPartPayParams(AvaliablePaymentMethodType.ALIPAY_CN, paymentIntentId, clientSecret, customerId, paymentConsentId)
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for AliPay HK
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId the customerId of [PaymentIntent], optional.
         */
        fun createAlipayHKParams(
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return createThirdPartPayParams(AvaliablePaymentMethodType.ALIPAY_HK, paymentIntentId, clientSecret, customerId, paymentConsentId)
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for Dana
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId the customerId of [PaymentIntent], optional.
         */
        fun createDanaParams(
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return createThirdPartPayParams(AvaliablePaymentMethodType.DANA, paymentIntentId, clientSecret, customerId, paymentConsentId)
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for GCash
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId the customerId of [PaymentIntent], optional.
         */
        fun createGCashParams(
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return createThirdPartPayParams(AvaliablePaymentMethodType.GCASH, paymentIntentId, clientSecret, customerId, paymentConsentId)
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for kakaopay
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId the customerId of [PaymentIntent], optional.
         */
        fun createKakaoParams(
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return createThirdPartPayParams(AvaliablePaymentMethodType.KAKAO, paymentIntentId, clientSecret, customerId, paymentConsentId)
        }

        /**
         * Return the [ConfirmPaymentIntentParams] for tng
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId the customerId of [PaymentIntent], optional.
         */
        fun createTngParams(
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null,
            paymentConsentId: String? = null
        ): ConfirmPaymentIntentParams {
            return createThirdPartPayParams(AvaliablePaymentMethodType.TNG, paymentIntentId, clientSecret, customerId, paymentConsentId)
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
                    AvaliablePaymentMethodType.CARD,
                    PaymentMethodReference(paymentMethodId, cvc)
                )
                .setPaymentConsentId(paymentConsentId)
                .build()
        }
    }
}
