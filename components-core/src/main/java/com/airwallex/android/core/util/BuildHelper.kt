package com.airwallex.android.core.util

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object BuildHelper {
    val manufacturer: String = Build.MANUFACTURER ?: ""
    val model: String = Build.MODEL ?: ""
    val brand: String = Build.BRAND ?: ""
    val sdkVersion: Int = Build.VERSION.SDK_INT
    val versionRelease: String = Build.VERSION.RELEASE ?: ""

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
    fun isVersionAtLeastM() = sdkVersion >= Build.VERSION_CODES.M

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun isVersionAtLeastO() = sdkVersion >= Build.VERSION_CODES.O

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    fun isVersionAtLeastR() = sdkVersion >= Build.VERSION_CODES.R

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isVersionAtLeastS() = sdkVersion >= Build.VERSION_CODES.S

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S_V2)
    fun isVersionAtLeastSV2() = sdkVersion >= Build.VERSION_CODES.S_V2

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    fun isVersionAtLeastTiramisu() = sdkVersion >= Build.VERSION_CODES.TIRAMISU
}