package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Params for create a payment method
 */
@Parcelize
data class PaymentMethodCreateRequest internal constructor(
    /**
     * Unique request ID specified by the merchant
     */
    val requestId: String? = null,

    /**
     * The customer this payment method belongs to. If set, this payment method is automatically added to the customer as one of the available payment methods.
     */
    val customerId: String? = null,

    /**
     * Type of the payment method. Must be [PaymentMethodType.CARD]
     */
    val type: AvaliablePaymentMethodType? = null,

    /**
     * Card information. This must be provided if [type] is set to [PaymentMethodType.CARD]
     */
    val card: PaymentMethod.Card? = null,

    /**
     * Billing information.
     */
    val billing: Billing? = null,

    /**
     * A set of key-value pairs that you can attach to this payment method
     */
    val metadata: @RawValue Map<String, Any>? = null

) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_CUSTOMER_ID = "customer_id"
        private const val FIELD_TYPE = "type"
        private const val FIELD_CARD = "card"
        private const val FIELD_BILLING = "billing"
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
                type?.let {
                    mapOf(FIELD_TYPE to it.value)
                }.orEmpty()
            )
            .plus(
                card?.let {
                    mapOf(FIELD_CARD to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                billing?.let {
                    mapOf(FIELD_BILLING to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                metadata?.let {
                    mapOf(FIELD_METADATA to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<PaymentMethodCreateRequest> {
        private var requestId: String? = null
        private var customerId: String? = null
        private var type: AvaliablePaymentMethodType? = null
        private var card: PaymentMethod.Card? = null
        private var billing: Billing? = null
        private var metadata: @RawValue Map<String, Any>? = null

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setType(type: AvaliablePaymentMethodType?): Builder = apply {
            this.type = type
        }

        fun setCard(card: PaymentMethod.Card?): Builder = apply {
            this.card = card
        }

        fun setBilling(billing: Billing?): Builder = apply {
            this.billing = billing
        }

        fun setMetadata(metadata: @RawValue Map<String, Any>?): Builder = apply {
            this.metadata = metadata
        }

        override fun build(): PaymentMethodCreateRequest {
            return PaymentMethodCreateRequest(
                requestId = requestId,
                customerId = customerId,
                type = type,
                card = card,
                billing = billing,
                metadata = metadata
            )
        }
    }
}
