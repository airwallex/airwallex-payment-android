package com.airwallex.paymentacceptance

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager

object Settings {

    // API Key
    private const val API_KEY = ""

    // Client Id
    private const val CLIENT_ID = ""

    // WeChat Pay App Id
    private const val WECHAT_APP_ID = ""

    // Return url
    private const val RETURN_URL = ""

    private const val CUSTOMER_ID = "customerId"
    private val context: Context by lazy { SampleApplication.instance }

    private const val METADATA_KEY_API_KEY = "com.airwallex.sample.metadata.api_key"
    private const val METADATA_KEY_CLIENT_ID_KEY = "com.airwallex.sample.metadata.client_id"
    private const val METADATA_KEY_WECHAT_APP_ID_KEY = "com.airwallex.sample.metadata.wechat_app_id"
    private const val METADATA_KEY_RETURN_URL = "com.airwallex.sample.metadata.return_url"

    /**
     * `IMPORTANT` Token cannot appear on the merchant side, this is just for Demo purposes only
     */
    var token: String? = null

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
    val sdkEnv: String
        get() {
            val defaultSdkEnv =
                SampleApplication.instance.resources.getStringArray(R.array.array_sdk_env)[0]
            return sharedPreferences.getString(
                context.getString(R.string.sdk_env_id),
                defaultSdkEnv
            )
                ?: defaultSdkEnv
        }

    val checkoutMode: String
        get() {
            val defaultCheckoutMode =
                SampleApplication.instance.resources.getStringArray(R.array.array_checkout_mode)[0]
            return sharedPreferences.getString(
                context.getString(R.string.checkout_mode),
                defaultCheckoutMode
            )
                ?: defaultCheckoutMode
        }

    val returnUrl: String
        get() {
            return sharedPreferences.getString(context.getString(R.string.return_url), getMetadata(METADATA_KEY_RETURN_URL))
                ?: RETURN_URL
        }

    val nextTriggerBy: String
        get() {
            val defaultNextTriggeredBy =
                SampleApplication.instance.resources.getStringArray(R.array.array_next_trigger_by)[0]
            return sharedPreferences.getString(
                context.getString(R.string.next_trigger_by),
                defaultNextTriggeredBy
            )
                ?: defaultNextTriggeredBy
        }

    val requiresCVC: String
        get() {
            val defaultRequireCVC =
                SampleApplication.instance.resources.getStringArray(R.array.array_requires_cvc)[0]
            return sharedPreferences.getString(
                context.getString(R.string.requires_cvc),
                defaultRequireCVC
            )
                ?: defaultRequireCVC
        }

    val autoCapture: String
        get() {
            val defaultAutoCapture =
                SampleApplication.instance.resources.getStringArray(R.array.array_auto_capture)[0]
            return sharedPreferences.getString(
                context.getString(R.string.auto_capture),
                defaultAutoCapture
            )
                ?: defaultAutoCapture
        }

    val apiKey: String
        get() {
            val value = sharedPreferences.getString(context.getString(R.string.api_key), getMetadata(METADATA_KEY_API_KEY))
                ?: API_KEY

            return value.trimmedOfQuotes()
        }

    val clientId: String
        get() {
            val value = sharedPreferences.getString(context.getString(R.string.client_id), getMetadata(METADATA_KEY_CLIENT_ID_KEY))
                ?: CLIENT_ID
        }

    val weChatAppId: String
        get() {
            val value = sharedPreferences.getString(context.getString(R.string.wechat_app_id), getMetadata(METADATA_KEY_WECHAT_APP_ID_KEY))
                ?: WECHAT_APP_ID

            return value.trimmedOfQuotes()
        }

    val price: String
        get() {
            val defaultPrice = SampleApplication.instance.getString(R.string.price_value)
            return sharedPreferences.getString(context.getString(R.string.price), defaultPrice)
                ?: defaultPrice
        }

    val currency: String
        get() {
            val defaultCurrency =
                SampleApplication.instance.getString(R.string.currency_value)
            return sharedPreferences.getString(
                context.getString(R.string.currency),
                defaultCurrency
            ) ?: defaultCurrency
        }

    val countryCode: String
        get() {
            val defaultCountryCode =
                SampleApplication.instance.getString(R.string.country_code_value)
            return sharedPreferences.getString(
                context.getString(R.string.country_code),
                defaultCountryCode
            ) ?: defaultCountryCode
        }

    private fun getMetadata(key: String): String? {
        return context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .metaData
            .getString(key)
            .takeIf { it?.isNotBlank() == true }
    }
}

private fun String.trimmedOfQuotes() =
    this.removePrefix("\"").removeSuffix("\"")

