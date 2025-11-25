package com.airwallex.paymentacceptance

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.android.core.Environment
import kotlin.properties.Delegates

object Settings {

    // API Key
    private const val API_KEY = ""

    // Client Id
    private const val CLIENT_ID = ""

    // WeChat Pay App Id
    private const val WECHAT_APP_ID = ""

    // Return url
    private const val RETURN_URL = ""

    const val CUSTOMER_ID = "customerId"
    private val context: Context by lazy { SampleApplication.instance }

    private const val METADATA_KEY_API_KEY = "com.airwallex.sample.metadata.api_key"
    private const val METADATA_KEY_CLIENT_ID_KEY = "com.airwallex.sample.metadata.client_id"
    private const val METADATA_KEY_WECHAT_APP_ID_KEY = "com.airwallex.sample.metadata.wechat_app_id"
    private const val METADATA_KEY_RETURN_URL = "com.airwallex.sample.metadata.return_url"

    /**
     * `IMPORTANT` Token cannot appear on the merchant side, this is just for Demo purposes only
     */
    var token: String? = null

    var checkoutMode: AirwallexCheckoutMode by Delegates.observable(AirwallexCheckoutMode.PAYMENT) { _, _, newValue ->
        if (newValue == AirwallexCheckoutMode.PAYMENT) {
            nextTriggerBy =
                SampleApplication.instance.resources.getStringArray(R.array.array_next_trigger_by)[1]
        }
    }

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(SampleApplication.instance)
    }

    /**
     * Cache customerId is just to prevent creating multiple customers
     */
    var cachedCustomerId: String?
        set(value) {
            if (value?.isEmpty() == true) {
                sharedPreferences.edit().remove(CUSTOMER_ID).apply()
            } else {
                sharedPreferences.edit().putString(CUSTOMER_ID, value).apply()
            }
        }
        get() {
            return sharedPreferences.getString(CUSTOMER_ID, null)
        }

    // Default Staging
    var sdkEnv: String
        set(value) {
            sharedPreferences.edit()
                .putString(context.getString(R.string.sdk_env_id), value)
                .apply()
        }
        get() {
            val defaultSdkEnv =
                SampleApplication.instance.resources.getStringArray(R.array.array_sdk_env)[0]
            return sharedPreferences.getString(
                context.getString(R.string.sdk_env_id),
                defaultSdkEnv
            )
                ?: defaultSdkEnv
        }

    fun getEnvironment(): Environment {
        val sdkEnvArray = context.resources.getStringArray(R.array.array_sdk_env)
        return when (sdkEnv) {
            sdkEnvArray.getOrNull(0) -> Environment.STAGING
            sdkEnvArray.getOrNull(1) -> Environment.DEMO
            else -> Environment.PRODUCTION
        }
    }

    var returnUrl: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.return_url), value)
                .apply()
        }
        get() {
            return sharedPreferences.getString(
                context.getString(R.string.return_url),
                getMetadata(METADATA_KEY_RETURN_URL)
            )
                ?: RETURN_URL
        }

    var nextTriggerBy: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.next_trigger_by), value)
                .apply()
        }
        get() {
            val defaultNextTriggeredBy =
                SampleApplication.instance.resources.getStringArray(R.array.array_next_trigger_by)[0]
            return sharedPreferences.getString(
                context.getString(R.string.next_trigger_by),
                defaultNextTriggeredBy
            )
                ?: defaultNextTriggeredBy
        }

    var paymentLayout: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.payment_layout), value)
                .apply()
        }
        get() {
            val defaultPaymentLayout =
                SampleApplication.instance.resources.getStringArray(R.array.array_payment_layout)[0]
            return sharedPreferences.getString(
                context.getString(R.string.payment_layout),
                defaultPaymentLayout
            )
                ?: defaultPaymentLayout
        }

    var requiresEmail: String
        set(value) {
            sharedPreferences.edit()
                .putString(context.getString(R.string.requires_email), value)
                .apply()
        }
        get() {
            val defaultRequiresEmail =
                SampleApplication.instance.resources.getStringArray(R.array.array_requires_email)[0]
            return sharedPreferences.getString(
                context.getString(R.string.requires_email),
                defaultRequiresEmail
            )
                ?: defaultRequiresEmail
        }

    var force3DS: String
        set(value) {
            sharedPreferences.edit()
                .putString(context.getString(R.string.force_3ds), value)
                .apply()
        }
        get() {
            val defaultForce3DS =
                SampleApplication.instance.resources.getStringArray(R.array.array_force_3ds)[0]
            return sharedPreferences.getString(
                context.getString(R.string.force_3ds),
                defaultForce3DS
            )
                ?: defaultForce3DS
        }

    var autoCapture: String
        set(value) {
            sharedPreferences.edit()
                .putString(context.getString(R.string.auto_capture), value)
                .apply()
        }
        get() {
            val defaultAutoCapture =
                SampleApplication.instance.resources.getStringArray(R.array.array_auto_capture)[0]
            return sharedPreferences.getString(
                context.getString(R.string.auto_capture),
                defaultAutoCapture
            )
                ?: defaultAutoCapture
        }

    var expressCheckout: String
        set(value) {
            sharedPreferences.edit()
                .putString("express_checkout", value)
                .apply()
        }
        get() {
            return sharedPreferences.getString("express_checkout", "Disabled") ?: "Disabled"
        }

    var apiKey: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.api_key), value).apply()
        }
        get() {
            val value = sharedPreferences.getString(
                context.getString(R.string.api_key),
                getMetadata(METADATA_KEY_API_KEY)
            )
                ?: API_KEY

            return value.cleaned().emptyIfReplaceWithApiKey()
        }

    var clientId: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.client_id), value).apply()
        }
        get() {
            val value = sharedPreferences.getString(
                context.getString(R.string.client_id),
                getMetadata(METADATA_KEY_CLIENT_ID_KEY)
            )
                ?: CLIENT_ID

            return value.cleaned().emptyIfReplaceWithClientID()
        }

    var weChatAppId: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.wechat_app_id), value)
                .apply()
        }
        get() {
            val value = sharedPreferences.getString(
                context.getString(R.string.wechat_app_id),
                getMetadata(METADATA_KEY_WECHAT_APP_ID_KEY)
            )
                ?: WECHAT_APP_ID

            return value.cleaned()
        }

    var price: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.price), value).apply()
        }
        get() {
            val defaultPrice = SampleApplication.instance.getString(R.string.price_value)
            return sharedPreferences.getString(
                context.getString(R.string.price),
                defaultPrice
            )?.takeIf { it.isNotBlank() } ?: defaultPrice
        }

    var currency: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.currency), value).apply()
        }
        get() {
            val defaultCurrency =
                SampleApplication.instance.getString(R.string.currency_value)
            return sharedPreferences.getString(
                context.getString(R.string.currency),
                defaultCurrency
            )?.takeIf { it.isNotBlank() } ?: defaultCurrency
        }

    var countryCode: String
        set(value) {
            sharedPreferences.edit().putString(context.getString(R.string.country_code), value)
                .apply()
        }
        get() {
            val defaultCountryCode =
                SampleApplication.instance.getString(R.string.country_code_value)
            return sharedPreferences.getString(
                context.getString(R.string.country_code),
                defaultCountryCode
            )?.takeIf { it.isNotBlank() } ?: defaultCountryCode
        }

    private fun getMetadata(key: String): String? {
        return context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .metaData
            .getString(key)
            .takeIf { it?.isNotBlank() == true }
    }
}

private fun String.cleaned() =
    this
        .trim()
        .removePrefix("\"")
        .removeSuffix("\"")

private fun String.emptyIfReplaceWithApiKey(): String {
    return if (this == "replace_with_api_key") "" else this
}

private fun String.emptyIfReplaceWithClientID(): String {
    return if (this == "replace_with_client_id") "" else this
}

