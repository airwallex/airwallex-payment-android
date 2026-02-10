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

private typealias AirwallexEnvironment = com.airwallex.android.core.Environment

/**
 * A singleton object responsible for handling analytics logging throughout the Airwallex SDK.
 * Provides methods to log various types of events including page views, errors, API errors, actions, and payment views.
 */
object AnalyticsLogger {
    private var tracker: Tracker? = null
    private var paymentIntentId: String? = null
    private var transactionMode: String? = null

    // region Public API

    /**
     * Initializes the analytics tracker with the provided context.
     * @param context The application context used for initialization.
     */
    fun initialize(context: Context) {
        if (AirwallexPlugins.enableAnalytics && tracker == null) {
            tracker = Tracker(
                config = Config(
                    appName = "pa_mobile_sdk",
                    appVersion = BuildConfigHelper.versionName,
                    environment = AirwallexPlugins.environment.toTrackerEnvironment()
                ),
                context = context
            )
            tracker?.extraCommonData = getExtraCommonData(context)
        }
    }

    /**
     * Updates the account ID in the analytics tracker.
     * @param accountId The account ID to be updated.
     */
    fun updateAccountId(accountId: String?) {
        tracker?.let {
            it.extraCommonData = it.extraCommonData.toMutableMap().apply {
                putIfNotNull("accountId", accountId)
            }
        }
    }

    /**
     * Logs a page view event.
     * @param pageName The name of the page being viewed.
     * @param additionalInfo Additional information to be included with the event.
     */
    fun logPageView(pageName: String, additionalInfo: Map<String, Any>? = null) {
        val extraInfo = (additionalInfo?.toMutableMap() ?: mutableMapOf()).apply {
            putAll(additionalSessionInfo)
            this["eventType"] = "page_view"
        }
        tracker?.info(pageName, extraInfo)
    }

    /**
     * Logs a generic error event.
     * @param eventName The name of the error event.
     * @param additionalInfo Additional information about the error.
     */
    fun logError(eventName: String, additionalInfo: Map<String, Any>) {
        tracker?.error(eventName, additionalInfo)
    }

    /**
     * Logs an error event with exception details.
     * @param eventName The name of the error event.
     * @param exception The exception that occurred.
     * @param additionalInfo Additional information about the error.
     */
    fun logError(
        eventName: String,
        exception: AirwallexException,
        additionalInfo: Map<String, Any>? = null
    ) {
        val extraInfo = mutableMapOf<String, Any>().apply {
            exception.getAirwallexCodeOrStatusCode()
                .takeIf { it.isNotEmpty() }
                ?.let { this["code"] = it }

            exception.getAirwallexMessageOrMessage()
                ?.takeIf { it.isNotEmpty() }
                ?.let { this["message"] = it }

            additionalInfo?.let { putAll(it) }
            putAll(additionalSessionInfo)
        }

        logError(eventName, extraInfo)
    }

    /**
     * Logs an API error event.
     * @param eventName The name of the API error event.
     * @param url The URL where the error occurred.
     * @param exception The exception that was thrown.
     */
    fun logApiError(
        eventName: String,
        url: String,
        exception: AirwallexException
    ) {
        val extraInfo = mutableMapOf<String, Any>("eventType" to "pa_api_request").apply {
            if (url.isNotEmpty()) this["url"] = url

            exception.getAirwallexCodeOrStatusCode()
                .takeIf { it.isNotEmpty() }
                ?.let { this["code"] = it }

            exception.getAirwallexMessageOrMessage()
                ?.takeIf { it.isNotEmpty() }
                ?.let { this["message"] = it }

            putAll(additionalSessionInfo)
        }

        logError(eventName, extraInfo)
    }

    /**
     * Logs a user action event.
     * @param actionName The name of the action performed.
     * @param additionalInfo Additional information about the action.
     */
    fun logAction(actionName: String, additionalInfo: Map<String, Any>? = null) {
        val extraInfo = (additionalInfo?.toMutableMap() ?: mutableMapOf()).apply {
            putAll(additionalSessionInfo)
            this["eventType"] = "action"
        }
        tracker?.info(actionName, extraInfo)
    }

    /**
     * Logs a payment method view event.
     * @param viewName The name of the payment method view.
     * @param additionalInfo Additional information about the view.
     */
    fun logPaymentView(viewName: String, additionalInfo: Map<String, Any>? = null) {
        val extraInfo = (additionalInfo?.toMutableMap() ?: mutableMapOf()).apply {
            putAll(additionalSessionInfo)
            this["eventType"] = "payment_method_view"
        }
        tracker?.info(viewName, extraInfo)
    }

    /**
     * Sets the current session information.
     * @param transactionMode The current transaction mode.
     * @param paymentIntentId The payment intent ID (optional).
     */
    fun setSessionInformation(transactionMode: String, paymentIntentId: String? = null) {
        this.paymentIntentId = paymentIntentId
        this.transactionMode = transactionMode
    }

    // endregion

    // region Private Helpers

    private fun AirwallexEnvironment.toTrackerEnvironment(): Environment {
        return when (this) {
            AirwallexEnvironment.STAGING -> Environment.STAGING
            AirwallexEnvironment.DEMO -> Environment.DEMO
            AirwallexEnvironment.PRODUCTION -> Environment.PROD
        }
    }

    private fun getExtraCommonData(context: Context): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            putIfNotNull("merchantAppName", context.packageManager.getAppName(context.packageName))
            putIfNotNull(
                "merchantAppVersion",
                context.packageManager.getAppVersion(context.packageName)
            )
            putIfNotNull("accountId", TokenManager.accountId)
        }
    }

    private val additionalSessionInfo: MutableMap<String, Any>
        get() = mutableMapOf<String, Any>().apply {
            putIfNotNull("payment_intent_id", paymentIntentId)
            putIfNotNull("transaction_mode", transactionMode)
        }

    // endregion
}

// region Extension Functions

private fun AirwallexException.getAirwallexCodeOrStatusCode(): String {
    return error?.code ?: statusCode.toString()
}

private fun AirwallexException.getAirwallexMessageOrMessage(): String? {
    return error?.message ?: message
}

// endregion