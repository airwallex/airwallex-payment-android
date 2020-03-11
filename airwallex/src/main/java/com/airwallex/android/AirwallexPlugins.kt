package com.airwallex.android

import com.google.gson.Gson
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

internal object AirwallexPlugins {

    private lateinit var configuration: AirwallexConfiguration

    internal fun initialize(configuration: AirwallexConfiguration) {
        this.configuration = configuration
    }

    internal val enableLogging by lazy { configuration.enableLogging }

    internal val restClient by lazy {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.interceptors().add(0, object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val builder = chain.request().newBuilder()
                builder.addHeader("Accept", "application/json")
                builder.addHeader("Content-Type", "application/json")
                return chain.proceed(builder.build())
            }
        })
        clientBuilder.connectTimeout(5, TimeUnit.SECONDS)
        clientBuilder.readTimeout(30, TimeUnit.SECONDS)
        clientBuilder.followRedirects(false)
        AirwallexHttpClient.createClient(clientBuilder)
    }

    internal val gson by lazy {
        Gson()
    }
}
