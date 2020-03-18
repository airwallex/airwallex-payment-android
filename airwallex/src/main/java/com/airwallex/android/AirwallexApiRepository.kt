package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.PaymentIntentConfirmRequest
import com.google.gson.JsonParser
import kotlinx.android.parcel.Parcelize
import java.util.*

internal class AirwallexApiRepository : ApiRepository {

    // TODO Token should be removed after server changed
    @Parcelize
    internal data class Options internal constructor(
        internal val token: String,
        internal val clientSecret: String,
        internal val baseUrl: String,
        internal val paymentIntentOptions: PaymentIntentOptions? = null
    ) : Parcelable

    @Parcelize
    internal data class PaymentIntentOptions internal constructor(
        internal val paymentIntentId: String
    ) : Parcelable

    override fun confirmPaymentIntent(
        options: Options,
        paymentIntentParams: PaymentIntentConfirmRequest
    ): AirwallexHttpResponse? {
        val jsonParser = JsonParser()
        val paramsJson =
            jsonParser.parse(AirwallexPlugins.gson.toJson(paymentIntentParams)).asJsonObject

        return AirwallexPlugins.restClient.execute(
            AirwallexHttpRequest.Builder(
                confirmPaymentIntentUrl(options),
                AirwallexHttpRequest.Method.POST
            )
                .setBody(
                    AirwallexHttpBody(
                        CONTENT_TYPE,
                        paramsJson.toString()
                    )
                )
                .addClientSecretHeader(options.clientSecret)
                .build()
        )
    }

    override fun retrievePaymentIntent(options: Options): AirwallexHttpResponse? {
        return AirwallexPlugins.restClient.execute(
            AirwallexHttpRequest.Builder(
                retrievePaymentIntentUrl(options),
                AirwallexHttpRequest.Method.GET
            )
                .addTokenHeader(options.token)
                .build()
        )
    }

    // TODO token should be removed.
    private fun AirwallexHttpRequest.Builder.addTokenHeader(
        token: String
    ): AirwallexHttpRequest.Builder {
        return addHeader("Authorization", "Bearer $token")
    }

    private fun AirwallexHttpRequest.Builder.addClientSecretHeader(
        clientSecret: String
    ): AirwallexHttpRequest.Builder {
        return addHeader("client-secret", clientSecret)
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}`
     */
    private fun retrievePaymentIntentUrl(options: Options): String {
        return getApiUrl(
            options.baseUrl,
            "payment_intents/%s",
            requireNotNull(options.paymentIntentOptions?.paymentIntentId)
        )
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}/confirm`
     */
    private fun confirmPaymentIntentUrl(options: Options): String {
        return getApiUrl(
            options.baseUrl,
            "payment_intents/%s/confirm",
            requireNotNull(options.paymentIntentOptions?.paymentIntentId)
        )
    }

    private fun getApiUrl(baseUrl: String, path: String, vararg args: Any): String {
        return "$baseUrl/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
    }

    companion object {
        private const val CONTENT_TYPE = "application/json; charset=utf-8"
    }
}
