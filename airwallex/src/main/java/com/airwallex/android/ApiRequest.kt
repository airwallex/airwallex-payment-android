package com.airwallex.android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class ApiRequest {

    @Parcelize
    internal data class Options internal constructor(
        val apiKey: String,
        internal val clientId: String? = null
    ) : Parcelable {

        init {
            ApiKeyValidator.requireValid(apiKey)
        }
    }
}