package com.airwallex.android.core.model

/**
 * The params that used for retrieve [PaymentMethodTypeInfo]
 */
data class RetrievePaymentMethodTypeInfoParams internal constructor(
    val clientSecret: String,
    internal val paymentMethodType: String,
    internal val flow: AirwallexPaymentRequestFlow?,
    internal val transactionMode: TransactionMode?,
    internal val countryCode: String?,
    internal val openId: String?
) {
    class Builder(
        private val clientSecret: String,
        private val paymentMethodType: String
    ) : ObjectBuilder<RetrievePaymentMethodTypeInfoParams> {

        private var flow: AirwallexPaymentRequestFlow? = null
        private var transactionMode: TransactionMode? = null
        private var countryCode: String? = null
        private var openId: String? = null

        fun setFlow(flow: AirwallexPaymentRequestFlow?): Builder = apply {
            this.flow = flow
        }

        fun setTransactionMode(transactionMode: TransactionMode?): Builder = apply {
            this.transactionMode = transactionMode
        }

        fun setCountryCode(countryCode: String?): Builder = apply {
            this.countryCode = countryCode
        }

        fun setOpenId(openId: String?): Builder = apply {
            this.openId = openId
        }

        override fun build(): RetrievePaymentMethodTypeInfoParams {
            return RetrievePaymentMethodTypeInfoParams(
                clientSecret = clientSecret,
                paymentMethodType = paymentMethodType,
                flow = flow,
                transactionMode = transactionMode,
                countryCode = countryCode,
                openId = openId
            )
        }
    }
}
