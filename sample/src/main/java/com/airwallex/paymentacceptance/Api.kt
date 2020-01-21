package com.airwallex.paymentacceptance

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface Api {

    @POST("https://api-staging.airwallex.com/api/v1/authentication/login")
    fun authentication(@Header("x-api-key") apiKey: String, @Header("x-client-id") clientId: String): Observable<ResponseBody>

    @POST("/api/v1/pa/payment_intents/create")
    fun createPaymentIntent(@Header("Authorization") authorization: String, @Body params: MutableMap<String, Any>): Observable<ResponseBody>
}
