package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Request for Third Part Pay
 */
@Parcelize
data class PoliRequest constructor(

    val name: String

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_NAME = "name"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                mapOf(FIELD_NAME to name)
            )
    }
}
