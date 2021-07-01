package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Params for create a [PaymentConsent]
 */
@Parcelize
data class PaymentConsentCreateRequest internal constructor(
    /**
     * Unique request ID specified by the merchant
     */
    val requestId: String? = null,

    /**
     * ID from Airwallex of the customer for whom the consent is created
     */
    val customerId: String? = null,

    /**
     * PaymentMethod for subsequent payments. Can be provided later by updating the PaymentConsent
     */
    val paymentMethodRequest: PaymentMethodRequest? = null,

    /**
     * The party to trigger subsequent payments. Can be one of merchant, customer. If type of payment_method is card, both merchant and customer is supported. Otherwise, only merchant is supported
     */
    val nextTriggeredBy: PaymentConsent.NextTriggeredBy? = null,

    /**
     * Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled.
     * Default: unscheduled
     */
    val merchantTriggerReason: PaymentConsent.MerchantTriggerReason? = null,

    /**
     * Only applicable when next_triggered_by is customer. If false, the customer must provide cvc for the subsequent payment with this PaymentConsent.
     * Default: false
     */
    val requiresCvc: Boolean? = null,

    /**
     * A set of key-value pairs that can be attached to this PaymentConsent
     */
    val metadata: @RawValue Map<String, Any>? = null,
) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_CUSTOMER_ID = "customer_id"
        private const val FIELD_PAYMENT_METHOD = "payment_method"
        private const val FIELD_NEXT_TRIGGERED_BY = "next_triggered_by"
        private const val FIELD_MERCHANT_TRIGGER_REASON = "merchant_trigger_reason"
        private const val FIELD_REQUIRES_CVC = "requires_cvc"
        private const val FIELD_METADATA = "metadata"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                requestId?.let {
                    mapOf(FIELD_REQUEST_ID to it)
                }.orEmpty()
            )
            .plus(
                customerId?.let {
                    mapOf(FIELD_CUSTOMER_ID to it)
                }.orEmpty()
            )
            .plus(
                paymentMethodRequest?.let {
                    mapOf(FIELD_PAYMENT_METHOD to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                nextTriggeredBy?.let {
                    mapOf(FIELD_NEXT_TRIGGERED_BY to it.value)
                }.orEmpty()
            )
            .plus(
                merchantTriggerReason?.let {
                    mapOf(FIELD_MERCHANT_TRIGGER_REASON to it.value)
                }.orEmpty()
            )
            .plus(
                requiresCvc?.let {
                    mapOf(FIELD_REQUIRES_CVC to it)
                }.orEmpty()
            )
            .plus(
                metadata?.let {
                    mapOf(FIELD_METADATA to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<PaymentConsentCreateRequest> {
        private var requestId: String? = null
        private var customerId: String? = null
        private var paymentMethodRequest: PaymentMethodRequest? = null
        private var nextTriggeredBy: PaymentConsent.NextTriggeredBy? = null
        private var merchantTriggerReason: PaymentConsent.MerchantTriggerReason? = null
        private var requiresCvc: Boolean? = null
        private var metadata: @RawValue Map<String, Any>? = null

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentMethodRequest(paymentMethodRequest: PaymentMethodRequest?): Builder = apply {
            this.paymentMethodRequest = paymentMethodRequest
        }

        fun setNextTriggeredBy(nextTriggeredBy: PaymentConsent.NextTriggeredBy?): Builder = apply {
            this.nextTriggeredBy = nextTriggeredBy
        }

        fun setMerchantTriggerReason(merchantTriggerReason: PaymentConsent.MerchantTriggerReason?): Builder = apply {
            this.merchantTriggerReason = merchantTriggerReason
        }

        fun setRequiresCvc(requiresCvc: Boolean): Builder = apply {
            this.requiresCvc = requiresCvc
        }

        fun setMetadata(metadata: @RawValue Map<String, Any>?): Builder = apply {
            this.metadata = metadata
        }

        override fun build(): PaymentConsentCreateRequest {
            return PaymentConsentCreateRequest(
                requestId = requestId,
                customerId = customerId,
                paymentMethodRequest = paymentMethodRequest,
                nextTriggeredBy = nextTriggeredBy,
                merchantTriggerReason = merchantTriggerReason,
                requiresCvc = requiresCvc,
                metadata = metadata
            )
        }
    }
}
