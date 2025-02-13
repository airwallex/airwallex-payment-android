package com.airwallex.paymentacceptance.api

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @POST("/api/v1/authentication/login")
    suspend fun authentication(
        @Header("x-api-key") apiKey: String,
        @Header("x-client-id") clientId: String
    ): ResponseBody

    @POST("/api/v1/pa/payment_intents/create")
    suspend fun createPaymentIntent(@Body params: MutableMap<String, Any>): ResponseBody

    @POST("/api/v1/pa/customers/create")
    suspend fun createCustomer(@Body params: MutableMap<String, Any>): ResponseBody

    @GET("/api/v1/pa/customers/{id}/generate_client_secret")
    suspend fun createClientSecret(@Path("id") customId: String): ResponseBody

    @GET("/api/v1/pa/customers/{id}/generate_client_secret")
    suspend fun createClientSecret(
        @Path("id") customId: String,
        @Query("apiKey") apiKey: String,
        @Query("clientId") clientId: String
    ): ResponseBody
}
