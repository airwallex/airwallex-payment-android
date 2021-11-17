package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Request for Bank
 */
@Parcelize
data class AirwallexPaymentRequest constructor(
    val additionalInfo: Map<String, String>? = null,
    val flow: AirwallexPaymentRequestFlow? = null,
    val osType: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_FLOW = "flow"
        private const val FIELD_OS_TYPE = "os_type"
    }

    override fun toParamMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        additionalInfo?.forEach {
            map[it.key] = it.value
        }
        return map
            .plus(
                mapOf(FIELD_FLOW to (flow ?: AirwallexPaymentRequestFlow.IN_APP).value)
            )
            .plus(
                mapOf(FIELD_OS_TYPE to "android")
            )
    }
}
