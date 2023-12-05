package com.airwallex.android.core.model

data class RetrieveAvailablePaymentConsentsParams internal constructor(
    val clientSecret: String,
    val merchantTriggerReason: PaymentConsent.MerchantTriggerReason?,
    val nextTriggerBy: PaymentConsent.NextTriggeredBy?,
    val pageNum: Int,
    val pageSize: Int
) {
    class Builder(
        private val clientSecret: String,
        private val pageNum: Int
    ) : ObjectBuilder<RetrieveAvailablePaymentConsentsParams> {

        private var pageSize: Int = 20
        private var merchantTriggerReason: PaymentConsent.MerchantTriggerReason? = null
        private var nextTriggerBy: PaymentConsent.NextTriggeredBy? = null

        override fun build(): RetrieveAvailablePaymentConsentsParams {
            return RetrieveAvailablePaymentConsentsParams(
                clientSecret = clientSecret,
                merchantTriggerReason = merchantTriggerReason,
                nextTriggerBy = nextTriggerBy,
                pageNum = pageNum,
                pageSize = pageSize
            )
        }
    }
}