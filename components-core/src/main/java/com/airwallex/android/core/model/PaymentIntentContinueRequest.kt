package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The request params to confirm [PaymentIntent]
 */
@Parcelize
data class PaymentIntentContinueRequest(

    /**
     * Unique request ID specified by the merchant
     */
    val requestId: String? = null,

    /**
     * 3D Secure Type
     */
    val type: PaymentIntentContinueType? = null,

    /**
     * 3D Secure
     */
    val threeDSecure: ThreeDSecure? = null,

    val device: Device? = null,

    val useDcc: Boolean? = null
) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_TYPE = "type"
        private const val FIELD_THREE_DS = "three_ds"
        private const val FIELD_DEVICE = "device_data"
        private const val FIELD_USE_DCC = "use_dcc"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                requestId?.let {
                    mapOf(FIELD_REQUEST_ID to it)
                }.orEmpty()
            )
            .plus(
                type?.let {
                    mapOf(FIELD_TYPE to it.value)
                }.orEmpty()
            )
            .plus(
                threeDSecure?.let {
                    mapOf(FIELD_THREE_DS to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                device?.let {
                    mapOf(FIELD_DEVICE to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                useDcc?.let {
                    mapOf(FIELD_USE_DCC to it)
                }.orEmpty()
            )
    }
}
