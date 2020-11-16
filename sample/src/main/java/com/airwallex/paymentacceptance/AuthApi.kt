package com.airwallex.paymentacceptance

import okhttp3.ResponseBody
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/v1/authentication/login")
    suspend fun authentication(@Header("x-api-key") apiKey: String, @Header("x-client-id") clientId: String): ResponseBody
}
