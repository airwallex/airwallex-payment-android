package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Request for Bank
 */
@Parcelize
data class AirwallexPaymentRequest constructor(
    val additionalInfo: Map<String, String>? = null,
    val countryCode: String? = null,
    val flow: AirwallexPaymentRequestFlow? = null,
    val osType: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_COUNTRY_CODE = "country_code"
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
                countryCode?.let {
                    mapOf(FIELD_COUNTRY_CODE to countryCode)
                }.orEmpty()
            )
            .plus(
                mapOf(FIELD_FLOW to AirwallexPaymentRequestFlow.IN_APP.value)
            )
            .plus(
                mapOf(FIELD_OS_TYPE to "android")
            )
    }
}
