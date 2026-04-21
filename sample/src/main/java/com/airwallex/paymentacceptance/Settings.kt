package com.airwallex.paymentacceptance

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.android.core.Environment
import kotlin.properties.Delegates
import androidx.core.content.edit

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
            val key = "${CUSTOMER_ID}_${sdkEnv}"
            if (value?.isEmpty() == true) {
                sharedPreferences.edit { remove(key) }
            } else {
                sharedPreferences.edit { putString(key, value) }
            }
        }
        get() {
            val key = "${CUSTOMER_ID}_${sdkEnv}"
            return sharedPreferences.getString(key, null)
        }

    /**
     * Get cached customer ID for a specific environment
     */
    fun getCachedCustomerIdForEnv(env: String): String {
        val key = "${CUSTOMER_ID}_${env}"
        return sharedPreferences.getString(key, null) ?: ""
    }

    /**
     * Save customer ID for a specific environment
     */
    fun saveCachedCustomerIdForEnv(env: String, value: String) {
        val key = "${CUSTOMER_ID}_${env}"
        if (value.isEmpty()) {
            sharedPreferences.edit { remove(key) }
        } else {
            sharedPreferences.edit { putString(key, value) }
        }
    }

    // Default Staging
    var sdkEnv: String
        set(value) {
            sharedPreferences.edit {
                putString(context.getString(R.string.sdk_env_id), value)
            }
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
            sdkEnvArray.getOrNull(2) -> Environment.PREVIEW
            else -> Environment.PRODUCTION
        }
    }

    var returnUrl: String
        set(value) {
            sharedPreferences.edit {
                putString(context.getString(R.string.return_url), value)
            }
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
            sharedPreferences.edit {
                putString(context.getString(R.string.next_trigger_by), value)
            }
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
            sharedPreferences.edit {
                putString(context.getString(R.string.payment_layout), value)
            }
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
            sharedPreferences.edit {
                putString(context.getString(R.string.requires_email), value)
            }
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
            sharedPreferences.edit {
                putString(context.getString(R.string.force_3ds), value)
            }
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
            sharedPreferences.edit {
                putString(context.getString(R.string.auto_capture), value)
            }
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
            sharedPreferences.edit {
                putString("express_checkout", value)
            }
        }
        get() {
            return sharedPreferences.getString("express_checkout", "Disabled") ?: "Disabled"
        }

    var useSession: String
        set(value) {
            sharedPreferences.edit {
                putString("use_session", value)
            }
        }
        get() {
            return sharedPreferences.getString("use_session", "Enabled") ?: "Enabled"
        }

    var apiKey: String
        set(value) {
            val key = "${context.getString(R.string.api_key)}_${sdkEnv}"
            sharedPreferences.edit { putString(key, value) }
        }
        get() {
            val key = "${context.getString(R.string.api_key)}_${sdkEnv}"
            val value = sharedPreferences.getString(
                key,
                getMetadata(METADATA_KEY_API_KEY)
            )
                ?: API_KEY

            return value.cleaned().emptyIfReplaceWithApiKey()
        }

    /**
     * Get cached API key for a specific environment
     */
    fun getApiKeyForEnv(env: String): String {
        val key = "${context.getString(R.string.api_key)}_${env}"
        val value = sharedPreferences.getString(key, null) ?: ""
        return value.cleaned().emptyIfReplaceWithApiKey()
    }

    /**
     * Save API key for a specific environment
     */
    fun saveApiKeyForEnv(env: String, value: String) {
        val key = "${context.getString(R.string.api_key)}_${env}"
        sharedPreferences.edit { putString(key, value) }
    }

    var clientId: String
        set(value) {
            val key = "${context.getString(R.string.client_id)}_${sdkEnv}"
            sharedPreferences.edit { putString(key, value) }
        }
        get() {
            val key = "${context.getString(R.string.client_id)}_${sdkEnv}"
            val value = sharedPreferences.getString(
                key,
                getMetadata(METADATA_KEY_CLIENT_ID_KEY)
            )
                ?: CLIENT_ID

            return value.cleaned().emptyIfReplaceWithClientID()
        }

    /**
     * Get cached client ID for a specific environment
     */
    fun getClientIdForEnv(env: String): String {
        val key = "${context.getString(R.string.client_id)}_${env}"
        val value = sharedPreferences.getString(key, null) ?: ""
        return value.cleaned().emptyIfReplaceWithClientID()
    }

    /**
     * Save client ID for a specific environment
     */
    fun saveClientIdForEnv(env: String, value: String) {
        val key = "${context.getString(R.string.client_id)}_${env}"
        sharedPreferences.edit { putString(key, value) }
    }

    var weChatAppId: String
        set(value) {
            sharedPreferences.edit {
                putString(context.getString(R.string.wechat_app_id), value)
            }
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
            sharedPreferences.edit { putString(context.getString(R.string.price), value) }
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
            sharedPreferences.edit { putString(context.getString(R.string.currency), value) }
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
            sharedPreferences.edit {
                putString(context.getString(R.string.country_code), value)
            }
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

