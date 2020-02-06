package com.airwallex.paymentacceptance

import com.airwallex.paymentacceptance.Constants.AUTH_URL
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface Api {

    @POST("$AUTH_URL/api/v1/authentication/login")
    fun authentication(@Header("x-api-key") apiKey: String, @Header("x-client-id") clientId: String): Observable<ResponseBody>

    @POST("/api/v1/pa/payment_intents/create")
    fun createPaymentIntent(@Header("Authorization") authorization: String, @Body params: MutableMap<String, Any>): Observable<ResponseBody>

    @GET("/api/v1/pa/payment_methods")
    fun fetchPaymentMethods(@Header("Authorization") authorization: String): Observable<ResponseBody>

    @POST("/api/v1/pa/payment_methods/create")
    fun savePaymentMethod(@Header("Authorization") authorization: String, @Body params: MutableMap<String, Any>): Observable<ResponseBody>
}
