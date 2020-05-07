package com.airwallex.android

import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Provide some internal plugins
 */
internal object AirwallexPlugins {

    private lateinit var configuration: AirwallexConfiguration

    internal fun initialize(configuration: AirwallexConfiguration) {
        this.configuration = configuration
    }

    /**
     * Enable logging in the Airwallex
     */
    internal val enableLogging: Boolean
        get() {
            return configuration.enableLogging
        }

    /**
     * Base URL in the Airwallex
     */
    internal val baseUrl: String
        get() {
            return configuration.baseUrl
        }

    /**
     * 3DS Environment
     */
    internal val threeDSecureEnv: CardinalEnvironment
        get() {
            return configuration.threeDSecureEnv
        }


    /**
     * Provide [AirwallexHttpClient] to request the Airwallex API
     */
    internal val httpClient by lazy {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.interceptors().add(0, object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val builder = chain.request().newBuilder()
                builder.addHeader(ACCEPT_HEADER_KEY, ACCEPT_HEADER_VALUE)
                builder.addHeader(CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VALUE)
                builder.addHeader(USER_AGENT_KEY, USER_AGENT_VALUE)
                builder.addHeader(USER_AGENT_VERSION_KEY, USER_AGENT_VERSION_VALUE)
                builder.addHeader(API_VERSION, BuildConfig.API_VERSION_CODE)
                return chain.proceed(builder.build())
            }
        })
        clientBuilder.connectTimeout(HTTP_CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
        clientBuilder.readTimeout(HTTP_READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
        clientBuilder.followRedirects(false)
        AirwallexHttpClient.createClient(clientBuilder)
    }

    /**
     * Provide [Gson] to parse the response of Airwallex API
     */
    internal val gson by lazy {
        Gson()
    }

    private const val HTTP_CONNECTION_TIMEOUT_SECOND = 5L
    private const val HTTP_READ_TIMEOUT_SECOND = 30L
    private const val ACCEPT_HEADER_KEY = "Accept"
    private const val ACCEPT_HEADER_VALUE = "application/json"
    private const val CONTENT_TYPE_HEADER_KEY = "Content-Type"
    private const val CONTENT_TYPE_HEADER_VALUE = "application/json"
    private const val USER_AGENT_KEY = "Airwallex-User-Agent"
    private const val USER_AGENT_VALUE = "Airwallex-Android-SDK"
    private const val USER_AGENT_VERSION_KEY = "Airwallex-User-Agent-Version"
    private const val USER_AGENT_VERSION_VALUE = BuildConfig.VERSION_NAME
    private const val API_VERSION = "Airwallex-Api-Version"
}
