package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Request for Bank
 */
@Parcelize
data class AirwallexPaymentRequest constructor(
    val bank: Bank? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val countryCode: String? = null,
    val flow: AirwallexPaymentRequestFlow? = null,
    val osType: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_BANK = "bank_name"
        private const val FIELD_COUNTRY_CODE = "country_code"
        private const val FIELD_NAME = "shopper_name"
        private const val FIELD_EMAIL = "shopper_email"
        private const val FIELD_PHONE = "shopper_phone"
        private const val FIELD_FLOW = "flow"
        private const val FIELD_OS_TYPE = "os_type"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                bank?.let {
                    mapOf(FIELD_BANK to it.value)
                }.orEmpty()
            )
            .plus(
                countryCode?.let {
                    mapOf(FIELD_COUNTRY_CODE to countryCode)
                }.orEmpty()
            )
            .plus(
                name?.let {
                    mapOf(FIELD_NAME to it)
                }.orEmpty()
            )
            .plus(
                email?.let {
                    mapOf(FIELD_EMAIL to it)
                }.orEmpty()
            )
            .plus(
                phone?.let {
                    mapOf(FIELD_PHONE to it)
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
