package com.airwallex.android.core.extension

import android.content.pm.PackageManager
import android.util.AndroidException
import androidx.core.content.pm.PackageInfoCompat
import com.airwallex.android.core.util.BuildHelper

fun PackageManager.getAppName(packageName: String): String? {
    val applicationInfo = try {
        getApplicationInfo(packageName, 0)
    } catch (ignore: PackageManager.NameNotFoundException) {
        return null
    }
    return getApplicationLabel(applicationInfo) as? String
}

fun PackageManager.getAppVersion(packageName: String): String? {
    val packageInfo = try {
        if (BuildHelper.isVersionAtLeastTiramisu()) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            getPackageInfo(packageName, 0)
        }
    } catch (ignore: AndroidException) {
        return null
    }
    return PackageInfoCompat.getLongVersionCode(packageInfo).toString()
}