package com.airwallex.android.core.model

data class RetrieveAvailablePaymentConsentsParams internal constructor(
    val clientSecret: String,
    val merchantTriggerReason: PaymentConsent.MerchantTriggerReason?,
    val nextTriggeredBy: PaymentConsent.NextTriggeredBy?,
    val status: PaymentConsent.PaymentConsentStatus?,
    val pageNum: Int,
    val pageSize: Int
) {
    class Builder(
        private val clientSecret: String,
        private val pageNum: Int
    ) : ObjectBuilder<RetrieveAvailablePaymentConsentsParams> {

        private var pageSize: Int = 20
        private var merchantTriggerReason: PaymentConsent.MerchantTriggerReason? = null
        private var nextTriggeredBy: PaymentConsent.NextTriggeredBy? = null
        private var status: PaymentConsent.PaymentConsentStatus? = null

        fun setNextTriggeredBy(nextTriggeredBy: PaymentConsent.NextTriggeredBy?): Builder = apply {
            this.nextTriggeredBy = nextTriggeredBy
        }

        fun setStatus(status: PaymentConsent.PaymentConsentStatus?): Builder = apply {
            this.status = status
        }

        override fun build(): RetrieveAvailablePaymentConsentsParams {
            return RetrieveAvailablePaymentConsentsParams(
                clientSecret = clientSecret,
                merchantTriggerReason = merchantTriggerReason,
                nextTriggeredBy = nextTriggeredBy,
                status = status,
                pageNum = pageNum,
                pageSize = pageSize
            )
        }
    }
}