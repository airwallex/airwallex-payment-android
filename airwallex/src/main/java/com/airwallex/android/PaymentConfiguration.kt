package com.airwallex.android

import android.content.Context
import android.content.SharedPreferences
import com.airwallex.android.util.ContextProvider
import org.jetbrains.annotations.NotNull

data class PaymentConfiguration(
    @NotNull val apiKey: String,
    @NotNull val clientId: String,
    val environment: Environment
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
            environment: Environment
        ) {
            prefs.edit()
                .putString(KEY_API_KEY, apiKey)
                .putString(KEY_CLIENT_ID, clientId)
                .putInt(KEY_ENVIRONMENT, environment.ordinal)
                .apply()
        }

        @JvmSynthetic
        internal fun load(): PaymentConfiguration? {
            val apiKey: String = prefs.getString(KEY_API_KEY, null) ?: return null
            val clientId: String = prefs.getString(KEY_CLIENT_ID, null) ?: return null
            val environment: Environment = Environment.getEnvironment(
                prefs.getInt(
                    KEY_ENVIRONMENT,
                    Environment.PRODUCTION.ordinal
                )
            )
            return PaymentConfiguration(
                apiKey,
                clientId,
                environment
            )
        }

        private companion object {
            private val NAME = PaymentConfiguration::class.java.canonicalName

            private const val KEY_API_KEY = "key_api_key"
            private const val KEY_CLIENT_ID = "key_client_id"
            private const val KEY_ENVIRONMENT = "key_environment"
        }
    }

    companion object {

        private var config: PaymentConfiguration? = null

        @JvmStatic
        fun getInstance(context: Context): PaymentConfiguration {
            return config
                ?: loadInstance(
                    context
                )
        }

        private fun loadInstance(context: Context): PaymentConfiguration {
            return Store(context).load()?.let {
                config = it
                it
            } ?: throw IllegalStateException("PaymentConfiguration was not initialized")
        }

        fun init(
            context: Context,
            apiKey: String,
            clientId: String,
            environment: Environment = Environment.PRODUCTION
        ) {
            config =
                PaymentConfiguration(
                    apiKey,
                    clientId,
                    environment
                )
            ContextProvider.init(context)
        }

        internal fun clearConfig() {
            config = null
        }
    }
}