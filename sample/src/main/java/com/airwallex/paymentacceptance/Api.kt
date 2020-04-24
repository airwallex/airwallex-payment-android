package com.airwallex.paymentacceptance

import com.airwallex.android.model.PaymentIntent
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {

    @POST("/api/v1/pa/payment_intents/create")
    fun createPaymentIntent(@Body params: MutableMap<String, Any>): Observable<PaymentIntent>

    @POST("/api/v1/pa/customers/create")
    fun createCustomer(@Body params: MutableMap<String, Any>): Observable<ResponseBody>
}
