package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * sdk information
 */
@Parcelize
data class IntegrationData constructor(

    /**
     * type of sdk
     */
    val type: String,

    /**
     * version of sdk
     */
    val version: String
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_TYPE = "type"
        private const val FIELD_VERSION = "version"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>(
            FIELD_TYPE to type,
            FIELD_VERSION to version
        )
    }
}
