package com.airwallex.paymentacceptance

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.support.v7.preference.PreferenceManager

object Settings {

    // Auth URL
    private const val AUTH_URL = ""
    // Base URL
    private const val BASE_URL = ""
    // API Key
    private const val API_KEY = ""
    // Client Id
    private const val CLIENT_ID = ""
    // WeChat Pay App Id
    private const val WECHAT_APP_ID = ""
    // WeChat Pay App Signature
    private const val WECHAT_APP_SIGNATURE = ""

    private const val CUSTOMER_ID = "customerId"
    private val context: Context by lazy { SampleApplication.instance }

    private const val METADATA_KEY_AUTH_URL_KEY = "com.airwallex.sample.metadata.auth_url"
    private const val METADATA_KEY_BASE_URL_KEY = "com.airwallex.sample.metadata.base_url"
    private const val METADATA_KEY_API_KEY = "com.airwallex.sample.metadata.api_key"
    private const val METADATA_KEY_CLIENT_ID_KEY = "com.airwallex.sample.metadata.client_id"
    private const val METADATA_KEY_WECHAT_APP_ID_KEY = "com.airwallex.sample.metadata.wechat_app_id"
    private const val METADATA_KEY_WECHAT_APP_SIGNATURE_KEY = "com.airwallex.sample.metadata.wechat_app_signature"

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
    var cachedCustomerId: String
        set(value) {
            sharedPreferences.edit()
                .putString(CUSTOMER_ID, value)
                .apply()
        }
        get() {
            return sharedPreferences.getString(CUSTOMER_ID, "") ?: ""
        }

    val authUrl: String
        get() {
            return sharedPreferences.getString(context.getString(R.string.auth_url), getMetadata(METADATA_KEY_AUTH_URL_KEY))
                ?: AUTH_URL
        }

    val baseUrl: String
        get() {
            return sharedPreferences.getString(context.getString(R.string.base_url), getMetadata(METADATA_KEY_BASE_URL_KEY))
                ?: BASE_URL
        }

    val apiKey: String
        get() {
            return sharedPreferences.getString(context.getString(R.string.api_key), getMetadata(METADATA_KEY_API_KEY))
                ?: API_KEY
        }

    val clientId: String
        get() {
            return sharedPreferences.getString(context.getString(R.string.client_id), getMetadata(METADATA_KEY_CLIENT_ID_KEY))
                ?: CLIENT_ID
        }

    val weChatAppId: String
        get() {
            return sharedPreferences.getString(context.getString(R.string.wechat_app_id), getMetadata(METADATA_KEY_WECHAT_APP_ID_KEY))
                ?: WECHAT_APP_ID
        }

    val weChatAppSignature: String
        get() {
            return sharedPreferences.getString(context.getString(R.string.wechat_app_signature), getMetadata(METADATA_KEY_WECHAT_APP_SIGNATURE_KEY))
                ?: WECHAT_APP_SIGNATURE
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

    private fun getMetadata(key: String): String? {
        return context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .metaData
            .getString(key)
            .takeIf { it?.isNotBlank() == true }
    }
}
