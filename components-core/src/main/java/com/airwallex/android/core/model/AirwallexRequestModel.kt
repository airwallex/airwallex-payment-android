package com.airwallex.android.core.model

import com.airwallex.android.core.util.BuildConfigHelper

interface AirwallexRequestModel {
    fun toParamMap(): Map<String, Any>

    val sdkType: String
        get() = MOBILE_SDK

    val sdkVersion: String
        get() = String.format(
            "android-%s-%s",
            mode,
            BuildConfigHelper.versionName
        )

    private val mode: String
        get() = if (BuildConfigHelper.isDebug) "debug" else "release"

    companion object {
        const val MOBILE_SDK = "mobile_sdk"
    }
}
