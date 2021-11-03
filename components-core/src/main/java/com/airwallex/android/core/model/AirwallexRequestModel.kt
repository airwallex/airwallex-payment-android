package com.airwallex.android.core.model

import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.BuildConfig

interface AirwallexRequestModel {
    fun toParamMap(): Map<String, Any>

    val sdkType: String
        get() = MOBILE_SDK

    val sdkVersion: String
        get() = String.format(
            "android-%s-%s",
            AirwallexPlugins.environment.value,
            BuildConfig.VERSION_NAME
        )

    companion object {
        const val MOBILE_SDK = "mobile_sdk"
    }
}
