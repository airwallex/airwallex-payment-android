package com.airwallex.android.core.log

import android.content.Context
import com.airwallex.airtracker.Config
import com.airwallex.airtracker.Environment
import com.airwallex.airtracker.Tracker
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.TokenManager
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.getAppName
import com.airwallex.android.core.extension.getAppVersion
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.util.BuildConfigHelper

private typealias AirwallexEnviornment = com.airwallex.android.core.Environment

object AnalyticsLogger {
    private var tracker: Tracker? = null
    private var paymentIntentId: String? = null
    private var transactionMode: String? = null

    fun initialize(context: Context) {
        if (AirwallexPlugins.enableAnalytics && tracker == null) {
            tracker = Tracker(
                Config(
                    appName = "pa_mobile_sdk",
                    appVersion = BuildConfigHelper.versionName,
                    environment = AirwallexPlugins.environment.toTrackerEnvironment()
                ),
                context
            )
            tracker?.extraCommonData = getExtraCommonData(context)
        }
    }

    fun updateAccountId(accountId: String?) {
        tracker?.let {
            it.extraCommonData = it.extraCommonData.toMutableMap().apply {
                putIfNotNull("accountId", accountId)
            }
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

    fun logError(
        eventName: String,
        exception: AirwallexException,
        additionalInfo: Map<String, Any>? = null
    ) {
        val extraInfo = mutableMapOf<String, Any>()
        exception.getAirwallexCodeOrStatusCode().takeIf { it.isNotEmpty() }
            ?.let { extraInfo["code"] = it }
        exception.getAirwallexMessageOrMessage()?.takeIf { it.isNotEmpty() }
            ?.let { extraInfo["message"] = it }
        additionalInfo?.let {
            extraInfo.putAll(it)
        }
        logError(eventName, extraInfo)
    }

    fun logApiError(
        eventName: String,
        url: String,
        exception: AirwallexException,
    ) {
        val extraInfo = mutableMapOf<String, Any>("eventType" to "pa_api_request")
        url.takeIf { it.isNotEmpty() }?.let { extraInfo["url"] = it }
        exception.getAirwallexCodeOrStatusCode().takeIf { it.isNotEmpty() }
            ?.let { extraInfo["code"] = it }
        exception.getAirwallexMessageOrMessage()?.takeIf { it.isNotEmpty() }
            ?.let { extraInfo["message"] = it }
        logError(eventName, extraInfo)
    }

    fun logAction(actionName: String, additionalInfo: Map<String, Any>? = null) {
        val extraInfo = additionalInfo?.toMutableMap() ?: mutableMapOf()
        extraInfo["eventType"] = "action"
        tracker?.info(actionName, extraInfo)
    }

    fun logPaymentView(viewName: String, additionalInfo: Map<String, Any>? = null) {
        val extraInfo = additionalInfo?.toMutableMap() ?: mutableMapOf()
        extraInfo["eventType"] = "payment_method_view"
        tracker?.info(viewName, extraInfo)
    }

    fun setSessionInformation(transactionMode: String, paymentIntentId: String? = null) {
        this.paymentIntentId = paymentIntentId
        this.transactionMode = transactionMode
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
            putIfNotNull("payment_intent_id", paymentIntentId)
            putIfNotNull("transaction_mode", transactionMode)
        }
    }
}

private fun AirwallexException.getAirwallexCodeOrStatusCode(): String {
    return error?.code ?: statusCode.toString()
}

private fun AirwallexException.getAirwallexMessageOrMessage(): String? {
    return error?.message ?: message
}