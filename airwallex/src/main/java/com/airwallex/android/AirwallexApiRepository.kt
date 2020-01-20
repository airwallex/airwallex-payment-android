package com.airwallex.android

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import com.google.gson.JsonObject
import kotlinx.android.parcel.Parcelize
import java.util.*

internal class AirwallexApiRepository : ApiRepository {

    @Parcelize
    internal data class Options internal constructor(
        internal val baseUrl: String,
        internal val token: String,
        internal val paymentIntentId: String
    ) : Parcelable

    override fun confirmPaymentIntent(options: Options): PaymentIntent? {
        val jsonRequest = JsonObject()
        val response = AirwallexPlugins.restClient.execute(
            AirwallexHttpRequest.Builder(
                getConfirmPaymentIntentUrl(options),
                AirwallexHttpRequest.Method.POST
            )
                .setBody(
                    AirwallexHttpBody(
                        "application/json; charset=utf-8",
                        jsonRequest.toString()
                    )
                )
                .addHeader("Authorization", "Bearer ${options.token}")
                .build()
        )
        return PaymentIntent(id = "")
    }

    override fun retrievePaymentIntent(options: Options): PaymentIntent? {
        val response = AirwallexPlugins.restClient.execute(
            AirwallexHttpRequest.Builder(
                getRetrievePaymentIntentUrl(options),
                AirwallexHttpRequest.Method.GET
            )
                .addHeader("Authorization", "Bearer ${options.token}")
                .build()
        )
        return PaymentIntent(id = "")
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getRetrievePaymentIntentUrl(options: Options): String {
        return getApiUrl(options.baseUrl, "payment_intents/%s", options.paymentIntentId)
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}/confirm`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getConfirmPaymentIntentUrl(options: Options): String {
        return getApiUrl(options.baseUrl, "payment_intents/%s/confirm", options.paymentIntentId)
    }

    private fun getApiUrl(baseUrl: String, path: String, vararg args: Any): String {
        return "${baseUrl}/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
    }
}