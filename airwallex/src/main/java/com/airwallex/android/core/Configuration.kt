package com.airwallex.android.core

import android.content.Context
import android.content.SharedPreferences
import com.airwallex.android.ApiKeyValidator
import com.airwallex.android.util.ContextProvider
import org.jetbrains.annotations.NotNull

data class Configuration(
    @NotNull val apiKey: String,
    @NotNull val clientId: String,
    @Environment val environment: String
) {

    init {
        ApiKeyValidator.requireValid(apiKey)
    }

    private class Store internal constructor(context: Context) {
        private val prefs: SharedPreferences =
            context.applicationContext.getSharedPreferences(NAME, 0)

        @JvmSynthetic
        internal fun save(
            apiKey: String,
            clientId: String,
            @Environment environment: String
        ) {
            prefs.edit()
                .putString(KEY_API_KEY, apiKey)
                .putString(KEY_CLIENT_ID, clientId)
                .putString(KEY_ENVIRONMENT, environment)
                .apply()
        }

        @JvmSynthetic
        internal fun load(): Configuration? {
            val apiKey: String = prefs.getString(KEY_API_KEY, null) ?: return null
            val clientId: String = prefs.getString(KEY_CLIENT_ID, null) ?: return null
            val environment: String = prefs.getString(KEY_ENVIRONMENT, null) ?: return null
            return Configuration(apiKey, clientId, environment)
        }

        private companion object {
            private val NAME = Configuration::class.java.canonicalName

            private const val KEY_API_KEY = "key_api_key"
            private const val KEY_CLIENT_ID = "key_client_id"
            private const val KEY_ENVIRONMENT = "key_environment"
        }
    }

    companion object {

        private var config: Configuration? = null

        @JvmStatic
        fun getInstance(context: Context): Configuration {
            return config ?: loadInstance(context)
        }

        private fun loadInstance(context: Context): Configuration {
            return Store(context).load()?.let {
                config = it
                it
            } ?: throw IllegalStateException("PaymentConfiguration was not initialized")
        }

        fun init(
            context: Context,
            apiKey: String,
            clientId: String,
            @Environment environment: String = Environment.PRODUCTION
        ) {
            config = Configuration(apiKey, clientId, environment)
            ContextProvider.init(context)
        }

        internal fun clearConfig() {
            config = null
        }
    }
}