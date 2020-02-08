package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PaymentMethodParams internal constructor(
    @SerializedName("request_id")
    val requestId: String? = null,

    @SerializedName("customer_id")
    val customerId: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("card")
    val card: PaymentMethod.Card?,

    @SerializedName("billing")
    val billing: PaymentMethod.Billing?,

    @SerializedName("metadata")
    val metadata: @RawValue Map<String, Any>?

) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<PaymentMethodParams> {
        private var requestId: String? = null
        private var customerId: String? = null
        private var type: String? = null
        private var card: PaymentMethod.Card? = null
        private var billing: PaymentMethod.Billing? = null
        private var metadata: @RawValue Map<String, Any>? = null

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setType(type: String?): Builder = apply {
            this.type = type
        }

        fun setCard(card: PaymentMethod.Card?): Builder = apply {
            this.card = card
        }

        fun setBilling(billing: PaymentMethod.Billing?): Builder = apply {
            this.billing = billing
        }

        fun setMetadata(metadata: @RawValue Map<String, Any>?): Builder = apply {
            this.metadata = metadata
        }

        override fun build(): PaymentMethodParams {
            return PaymentMethodParams(
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