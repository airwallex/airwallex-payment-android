package com.airwallex.android.core.util

import com.airwallex.android.core.BuildConfig

object BuildConfigHelper {
    val versionName: String = BuildConfig.VERSION_NAME
    val isDebug: Boolean = BuildConfig.DEBUG
    val apiVersion: String = BuildConfig.API_VERSION
}