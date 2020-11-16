package com.airwallex.paymentacceptance

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {

    @POST("/api/v1/pa/payment_intents/create")
    suspend fun createPaymentIntent(@Body params: MutableMap<String, Any>): ResponseBody

    @POST("/api/v1/pa/customers/create")
    suspend fun createCustomer(@Body params: MutableMap<String, Any>): ResponseBody

    @GET("/api/v1/pa/customers/{id}/generate_client_secret")
    suspend fun createClientSecret(@Path("id") customId: String): ResponseBody
}
