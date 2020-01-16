package com.airwallex.android

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit


internal object AirwallexPlugins {

    private lateinit var configuration: AirwallexConfiguration

    fun initialize(configuration: AirwallexConfiguration) {
        this.configuration = configuration
    }

    val baseUrl by lazy { configuration.environment.baseUrl }
    val authUrl by lazy { configuration.environment.authUrl }

    val enableLogging by lazy { configuration.enableLogging }

    private var restClient: AirwallexHttpClient? = null

    fun restClient(): AirwallexHttpClient? {
        if (restClient == null) {
            val clientBuilder = OkHttpClient.Builder()
            clientBuilder.interceptors().add(0, object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val builder = chain.request().newBuilder()
                    builder.addHeader("Accept", "application/json")
                    builder.addHeader("Content-Type", "application/json")
                    builder.addHeader("Airwallex-User-Agent", "Airwallex-Android-SDK")
                    builder.addHeader("Airwallex-User-Agent-Version", BuildConfig.VERSION_NAME)
                    return chain.proceed(builder.build())
                }
            })
            clientBuilder.connectTimeout(5, TimeUnit.SECONDS)
            clientBuilder.readTimeout(30, TimeUnit.SECONDS)
            clientBuilder.followRedirects(false)
            restClient = AirwallexHttpClient.createClient(clientBuilder)
        }
        return restClient
    }
}