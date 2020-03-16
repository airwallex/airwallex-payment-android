package com.airwallex.android

import com.google.gson.Gson
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

internal object AirwallexPlugins {

    private const val HTTP_CONNECTION_TIMEOUT_SECOND = 5L

    private const val HTTP_READ_TIMEOUT_SECOND = 30L

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
        clientBuilder.connectTimeout(HTTP_CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
        clientBuilder.readTimeout(HTTP_READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
        clientBuilder.followRedirects(false)
        AirwallexHttpClient.createClient(clientBuilder)
    }

    internal val gson by lazy {
        Gson()
    }
}
