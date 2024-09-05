package com.airwallex.paymentacceptance.api

import com.airwallex.paymentacceptance.Settings
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class ApiFactory internal constructor(private val baseUrl: String) {

    fun buildRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.interceptors().add(
            0,
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.addHeader("Accept", "application/json")
                Settings.token?.let {
                    builder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(builder.build())
            }
        )
        clientBuilder.connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        clientBuilder.readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        clientBuilder.addInterceptor(logging)
        val httpClient = clientBuilder.build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .client(httpClient)
            .build()
    }

    private companion object {
        private const val TIMEOUT_SECONDS = 15L
    }
}
