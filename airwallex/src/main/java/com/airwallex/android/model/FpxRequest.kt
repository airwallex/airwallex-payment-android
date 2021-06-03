package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Request for Third Part Pay
 */
@Parcelize
data class FpxRequest constructor(

    val name: String,
    val email: String,
    val phone: String

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_NAME = "name"
        private const val FIELD_EMAIL = "email"
        private const val FIELD_PHONE = "phone"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                mapOf(FIELD_NAME to name)
            )
            .plus(
                mapOf(FIELD_EMAIL to email)
            )
            .plus(
                mapOf(FIELD_PHONE to phone)
            )
    }
}
