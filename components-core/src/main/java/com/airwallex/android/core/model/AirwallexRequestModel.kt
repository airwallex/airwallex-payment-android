package com.airwallex.android.core.model

import com.airwallex.android.core.BuildConfig

interface AirwallexRequestModel {
    fun toParamMap(): Map<String, Any>

    val sdkType: String
        get() = MOBILE_SDK

    val sdkVersion: String
        get() = String.format(
            "android-%s-%s",
            mode,
            BuildConfig.VERSION_NAME
        )

    private val mode: String
        get() = if (BuildConfig.DEBUG) "debug" else "release"

    companion object {
        const val MOBILE_SDK = "mobile_sdk"
    }
}
