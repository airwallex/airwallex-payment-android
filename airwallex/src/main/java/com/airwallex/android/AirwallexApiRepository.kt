package com.airwallex.android

import androidx.annotation.VisibleForTesting
import com.google.gson.JsonObject
import java.util.*

internal class AirwallexApiRepository : ApiRepository {

    override fun confirmPaymentIntent(
        token: String,
        paymentIntentId: String
    ): PaymentIntent? {
        val jsonRequest = JsonObject()
        val response = AirwallexPlugins.restClient()?.execute(
            AirwallexHttpRequest.Builder(
                getConfirmPaymentIntentUrl(paymentIntentId),
                AirwallexHttpRequest.Method.POST
            )
                .setBody(
                    AirwallexHttpBody(
                        "application/json; charset=utf-8",
                        jsonRequest.toString()
                    )
                )
                .build()
        )
        return PaymentIntent(id = "")
    }

    override fun retrievePaymentIntent(
        token: String,
        paymentIntentId: String
    ): PaymentIntent? {
        val response = AirwallexPlugins.restClient()?.execute(
            AirwallexHttpRequest.Builder(
                getRetrievePaymentIntentUrl(paymentIntentId),
                AirwallexHttpRequest.Method.GET
            )
                .addHeader("Authorization", "Bearer $token")
                .build()
        )
        return PaymentIntent(id = "")
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getRetrievePaymentIntentUrl(paymentIntentId: String): String {
        return getApiUrl("payment_intents/%s", paymentIntentId)
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}/confirm`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getConfirmPaymentIntentUrl(paymentIntentId: String): String {
        return getApiUrl("payment_intents/%s/confirm", paymentIntentId)
    }

    private fun getApiUrl(path: String, vararg args: Any): String {
        return getApiUrl(String.format(Locale.ENGLISH, path, *args))
    }

    private fun getApiUrl(path: String): String {
        return "${AirwallexPlugins.baseUrl}/api/v1/pa/$path"
    }
}