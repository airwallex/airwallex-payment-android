package com.airwallex.paymentacceptance

import com.airwallex.android.model.PaymentIntent
import com.airwallex.paymentacceptance.Constants.AUTH_URL
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface Api {

    @POST("$AUTH_URL/api/v1/authentication/login")
    fun authentication(@Header("x-api-key") apiKey: String, @Header("x-client-id") clientId: String): Observable<ResponseBody>

    @POST("/api/v1/pa/payment_intents/create")
    fun createPaymentIntent(@Header("Authorization") authorization: String, @Body params: MutableMap<String, Any>): Observable<PaymentIntent>

    @GET("/api/v1/pa/payment_methods")
    fun fetchPaymentMethods(@Header("Authorization") authorization: String): Observable<ResponseBody>

    @POST("/api/v1/pa/customers/create")
    fun createCustomer(@Header("Authorization") authorization: String, @Body params: MutableMap<String, Any>): Observable<ResponseBody>
}
