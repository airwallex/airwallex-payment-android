package com.airwallex.android.redirect.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.RedirectMode
import com.airwallex.android.redirect.exception.RedirectException

object RedirectUtil {

    enum class ResolveResultType {
        RESOLVER_ACTIVITY, DEFAULT_BROWSER, APPLICATION, UNKNOWN
    }

    private const val RESOLVER_ACTIVITY_PACKAGE_NAME = "android"

    private fun determineResolveResult(context: Context, uri: Uri, packageName: String?): ResolveResultType {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(packageName)
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, "http://".toUri())
        try {
            val packageManager = context.packageManager
            val resolveInfo = packageManager.resolveActivity(intent, 0)
            val browserInfo =
                packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
            val resolvedPackageName = resolveInfo?.activityInfo?.packageName
            val browserPackageName = browserInfo?.activityInfo?.packageName
            if (resolvedPackageName != null) {
                return when (resolvedPackageName) {
                    RESOLVER_ACTIVITY_PACKAGE_NAME -> {
                        ResolveResultType.RESOLVER_ACTIVITY
                    }
                    browserPackageName -> {
                        ResolveResultType.DEFAULT_BROWSER
                    }
                    else -> {
                        ResolveResultType.APPLICATION
                    }
                }
            }
        } catch (_: Exception) {
            return ResolveResultType.UNKNOWN
        }
        return ResolveResultType.UNKNOWN
    }

    @Suppress("DEPRECATION")
    fun createRedirectIntent(context: Context, uri: Uri, packageName: String?): Intent {
        val redirectMode = AirwallexPlugins.redirectMode
        return if (determineResolveResult(context, uri, packageName) === ResolveResultType.APPLICATION) {
            Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage(packageName)
            }
        } else if (redirectMode == RedirectMode.EXTERNAL_BROWSER) {
            // Open in external browser instead of Custom Tabs
            Intent(Intent.ACTION_VIEW, uri)
        } else {
            val builder = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(ThemeUtil.getPrimaryThemeColor(context))

            // Set full screen height to disable PiP and show full screen bottom sheet
            if (redirectMode == RedirectMode.CUSTOM_TAB_BOTTOM_SHEET) {
                val screenHeight = context.resources.displayMetrics.heightPixels
                builder.setInitialActivityHeightPx(screenHeight, CustomTabsIntent.ACTIVITY_HEIGHT_DEFAULT)
                // Set high breakpoint to force bottom sheet behavior (not side sheet)
                builder.setActivitySideSheetBreakpointDp(Int.MAX_VALUE)
            }

            val customTabsIntent = builder.build()
            customTabsIntent.intent.data = uri
            customTabsIntent.intent
        }
    }

    private const val CUSTOM_TAB_REQUEST_CODE = 1008

    @Throws(RedirectException::class)
    fun makeRedirect(
        activity: Activity,
        redirectUrl: String,
        fallBackUrl: String? = null,
        packageName: String? = null
    ) {
        val redirectMode = AirwallexPlugins.redirectMode
        val redirectUri = redirectUrl.toUri()
        val redirectIntent = createRedirectIntent(activity, redirectUri, packageName)
        try {
            // Use startActivityForResult to disable PiP minimize button (not needed for external browser)
            if (redirectMode == RedirectMode.CUSTOM_TAB_BOTTOM_SHEET &&
                determineResolveResult(activity, redirectUri, packageName) != ResolveResultType.APPLICATION
            ) {
                activity.startActivityForResult(redirectIntent, CUSTOM_TAB_REQUEST_CODE)
            } else {
                activity.startActivity(redirectIntent)
            }
        } catch (e: ActivityNotFoundException) {
            if (!fallBackUrl.isNullOrEmpty()) {
                makeRedirect(activity, fallBackUrl, null, packageName)
            } else {
                throw RedirectException(message = "Redirect to app failed. ${e.localizedMessage}")
            }
        }
    }
}
