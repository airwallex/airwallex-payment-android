package com.airwallex.paymentacceptance

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object Settings {

    private const val TOKEN_KEY = "tokenKey"
    private const val CUSTOMER_ID = "customerId"
    private val context: Context by lazy { SampleApplication.instance }

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

    /**
     * `IMPORTANT` Token cannot appear on the merchant side, this is just for Demo purposes only
     */
    var token: String?
        get() {
            return sharedPreferences.getString(TOKEN_KEY, null)
        }
        set(newValue) {
            if (newValue == null) {
                sharedPreferences.edit().remove(TOKEN_KEY).apply()
            } else {
                sharedPreferences.edit().putString(TOKEN_KEY, newValue).apply()
            }
        }

    val authUrl: String
        get() {
            val defaultAuthUrl =
                SampleApplication.instance.getString(R.string.auth_url_value)
            return sharedPreferences.getString(context.getString(R.string.auth_url), defaultAuthUrl)
                ?: defaultAuthUrl
        }

    val baseUrl: String
        get() {
            val defaultBaseUrl =
                SampleApplication.instance.getString(R.string.base_url_value)
            return sharedPreferences.getString(context.getString(R.string.base_url), defaultBaseUrl)
                ?: defaultBaseUrl
        }

    val apiKey: String
        get() {
            val defaultApiKey = SampleApplication.instance.getString(R.string.api_key_value)
            return sharedPreferences.getString(context.getString(R.string.api_key), defaultApiKey)
                ?: defaultApiKey
        }

    val clientId: String
        get() {
            val defaultClientId =
                SampleApplication.instance.getString(R.string.client_id_value)
            return sharedPreferences.getString(
                context.getString(R.string.client_id),
                defaultClientId
            ) ?: defaultClientId
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

    val wechatAppId: String
        get() {
            val defaultAppId =
                SampleApplication.instance.resources.getStringArray(R.array.array_wechat_app_id)[0]
            return sharedPreferences.getString(
                context.getString(R.string.wechat_app_id),
                defaultAppId
            )
                ?: defaultAppId
        }

    val wechatAppSignature: String
        get() {
            val defaultAppSignature =
                SampleApplication.instance.getString(R.string.wechat_app_signature_value)
            return sharedPreferences.getString(
                context.getString(R.string.wechat_app_signature),
                defaultAppSignature
            )
                ?: defaultAppSignature
        }
}
