package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
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
    @SerializedName("request_id")
    val requestId: String? = null,

    /**
     * The customer this payment method belongs to. If set, this payment method is automatically added to the customer as one of the available payment methods.
     */
    @SerializedName("customer_id")
    val customerId: String? = null,

    /**
     * Type of the payment method. Must be [PaymentMethodType.CARD]
     */
    @SerializedName("type")
    val type: PaymentMethodType? = null,

    /**
     * Card information. This must be provided if [type] is set to [PaymentMethodType.CARD]
     */
    @SerializedName("card")
    val card: PaymentMethod.Card?,

    /**
     * Billing information.
     */
    @SerializedName("billing")
    val billing: Billing?,

    /**
     * A set of key-value pairs that you can attach to this payment method
     */
    @SerializedName("metadata")
    val metadata: @RawValue Map<String, Any>?

) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<PaymentMethodCreateRequest> {
        private var requestId: String? = null
        private var customerId: String? = null
        private var type: PaymentMethodType? = null
        private var card: PaymentMethod.Card? = null
        private var billing: Billing? = null
        private var metadata: @RawValue Map<String, Any>? = null

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setType(type: PaymentMethodType?): Builder = apply {
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
