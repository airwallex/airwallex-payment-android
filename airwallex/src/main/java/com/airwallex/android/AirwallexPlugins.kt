package com.airwallex.android

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

internal object AirwallexPlugins {

    internal val restClient by lazy {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.interceptors().add(0, object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val builder = chain.request().newBuilder()
                builder.addHeader(ACCEPT_HEADER_KEY, ACCEPT_HEADER_VALUE)
                builder.addHeader(CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VALUE)
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

    private const val HTTP_CONNECTION_TIMEOUT_SECOND = 5L
    private const val HTTP_READ_TIMEOUT_SECOND = 30L
    private const val ACCEPT_HEADER_KEY = "Accept"
    private const val ACCEPT_HEADER_VALUE = "application/json"
    private const val CONTENT_TYPE_HEADER_KEY = "Content-Type"
    private const val CONTENT_TYPE_HEADER_VALUE = "application/json"
}
