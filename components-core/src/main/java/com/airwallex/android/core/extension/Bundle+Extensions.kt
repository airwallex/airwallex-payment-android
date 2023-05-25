package com.airwallex.android.core.extension

import android.os.Bundle
import android.os.Parcelable
import com.airwallex.android.core.util.BuildHelper

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    BuildHelper.isVersionAtLeastTiramisu() -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}