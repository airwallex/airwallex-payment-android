package com.airwallex.android.core.log

import android.content.Context
import com.airwallex.airtracker.Config
import com.airwallex.airtracker.Environment
import com.airwallex.airtracker.Tracker
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.BuildConfig
import com.airwallex.android.core.TokenManager
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.getAppName
import com.airwallex.android.core.extension.getAppVersion
import com.airwallex.android.core.extension.putIfNotNull

private typealias AirwallexEnviornment = com.airwallex.android.core.Environment

object AnalyticsLogger {
    private var tracker: Tracker? = null

    fun initialize(context: Context) {
        if (AirwallexPlugins.enableAnalytics && tracker == null) {
            tracker = Tracker(
                Config(
                    appName = "pa_mobile_sdk",
                    appVersion = BuildConfig.VERSION_NAME,
                    environment = AirwallexPlugins.environment.toTrackerEnvironment()
                ),
                context
            )
            tracker?.extraCommonData = getExtraCommonData(context)
        }
    }

    fun logPageView(pageName: String, additionalInfo: Map<String, Any>? = null) {
        val extraInfo = additionalInfo?.toMutableMap() ?: mutableMapOf()
        extraInfo["eventType"] = "page_view"
        tracker?.info(pageName, extraInfo)
    }

    fun logError(eventName: String, additionalInfo: Map<String, Any>) {
        tracker?.error(eventName, additionalInfo)
    }

    fun logError(eventName: String, url: String?, exception: AirwallexException) {
        val extraInfo = mutableMapOf<String, Any>("eventType" to "pa_api_request")
        url?.takeIf { it.isNotEmpty() }?.let { extraInfo["url"] = it }
        exception.getAirwallexCodeOrStatusCode().takeIf { it.isNotEmpty() }?.let { extraInfo["code"] = it }
        exception.getAirwallexMessageOrMessage()?.takeIf { it.isNotEmpty() }?.let { extraInfo["message"] = it }
        tracker?.error(eventName, extraInfo)
    }

    fun logError(exception: AirwallexException, eventName: String) {
        logError(eventName, null, exception)
    }

    fun logAction(actionName: String) {
        tracker?.info(actionName, mapOf("eventType" to "action"))
    }

    fun logAction(actionName: String, additionalInfo: Map<String, Any>) {
        val extraInfo = additionalInfo.toMutableMap()
        extraInfo["eventType"] = "action"
        tracker?.info(actionName, extraInfo)
    }

    private fun AirwallexEnviornment.toTrackerEnvironment(): Environment {
        return when (this) {
            AirwallexEnviornment.STAGING -> Environment.STAGING
            AirwallexEnviornment.DEMO -> Environment.DEMO
            AirwallexEnviornment.PRODUCTION -> Environment.PROD
        }
    }

    private fun getExtraCommonData(context: Context): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            putIfNotNull("merchantAppName", context.packageManager.getAppName(context.packageName))
            putIfNotNull("merchantAppVersion", context.packageManager.getAppVersion(context.packageName))
            putIfNotNull("accountId", TokenManager.accountId)
        }
    }
}

private fun AirwallexException.getAirwallexCodeOrStatusCode(): String {
    return error?.code ?: statusCode.toString()
}

private fun AirwallexException.getAirwallexMessageOrMessage(): String? {
    return error?.message ?: message
}