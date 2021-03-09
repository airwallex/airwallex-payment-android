package com.airwallex.paymentacceptance

import okhttp3.ResponseBody
import retrofit2.http.*

interface Api {

    @POST("/api/v1/authentication/login")
    suspend fun authentication(@Header("x-api-key") apiKey: String, @Header("x-client-id") clientId: String): ResponseBody

    @POST("/api/v1/pa/payment_intents/create")
    suspend fun createPaymentIntent(@Body params: MutableMap<String, Any>): ResponseBody

    @POST("/api/v1/pa/customers/create")
    suspend fun createCustomer(@Body params: MutableMap<String, Any>): ResponseBody

    @GET("/api/v1/pa/customers/{id}/generate_client_secret")
    suspend fun createClientSecret(@Path("id") customId: String): ResponseBody
}
