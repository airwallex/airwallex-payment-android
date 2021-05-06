package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Params for disable a payment method
 */
@Parcelize
data class PaymentMethodDisableRequest internal constructor(
    /**
     * Unique request ID specified by the merchant
     */
    val requestId: String? = null,
) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                requestId?.let {
                    mapOf(FIELD_REQUEST_ID to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<PaymentMethodDisableRequest> {
        private var requestId: String? = null

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        override fun build(): PaymentMethodDisableRequest {
            return PaymentMethodDisableRequest(
                requestId = requestId
            )
        }
    }
}
