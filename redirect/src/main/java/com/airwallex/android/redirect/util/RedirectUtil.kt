package com.airwallex.android.redirect.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.browser.customtabs.CustomTabsIntent
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
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
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
        } catch (e: Exception) {
            return ResolveResultType.UNKNOWN
        }
        return ResolveResultType.UNKNOWN
    }

    @Suppress("DEPRECATION")
    @VisibleForTesting
    fun createRedirectIntent(context: Context, uri: Uri, packageName: String?): Intent {
        return if (determineResolveResult(context, uri, packageName) === ResolveResultType.APPLICATION) {
            Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage(packageName)
            }
        } else {
            val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(ThemeUtil.getPrimaryThemeColor(context))
                .build()
            customTabsIntent.intent.data = uri
            customTabsIntent.intent
        }
    }

    @Throws(RedirectException::class)
    fun makeRedirect(
        activity: Activity,
        redirectUrl: String,
        fallBackUrl: String? = null,
        packageName: String? = null
    ) {
        val redirectUri = Uri.parse(redirectUrl)
        val redirectIntent = createRedirectIntent(activity, redirectUri, packageName)
        try {
            activity.startActivity(redirectIntent)
        } catch (e: ActivityNotFoundException) {
            if (!fallBackUrl.isNullOrEmpty()) {
                makeRedirect(activity, fallBackUrl, null, packageName)
            } else {
                throw RedirectException(message = "Redirect to app failed. ${e.localizedMessage}")
            }
        }
    }
}
