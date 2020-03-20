package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.PaymentIntentConfirmRequest
import com.google.gson.JsonParser
import kotlinx.android.parcel.Parcelize
import java.util.*

internal class AirwallexApiRepository : ApiRepository {

    @Parcelize
    internal data class Options internal constructor(
        internal val clientSecret: String,
        internal val baseUrl: String,
        internal val paymentIntentOptions: PaymentIntentOptions? = null
    ) : Parcelable

    @Parcelize
    internal data class PaymentIntentOptions internal constructor(
        internal val paymentIntentId: String,
        internal val paymentIntentConfirmRequest: PaymentIntentConfirmRequest? = null
    ) : Parcelable

    override fun confirmPaymentIntent(options: Options): AirwallexHttpResponse? {
        val jsonParser = JsonParser()
        val paramsJson =
            jsonParser.parse(AirwallexPlugins.gson.toJson(requireNotNull(options.paymentIntentOptions?.paymentIntentConfirmRequest)))
                .asJsonObject

        return AirwallexPlugins.restClient.execute(
            AirwallexHttpRequest.Builder(
                confirmPaymentIntentUrl(options.baseUrl, requireNotNull(options.paymentIntentOptions?.paymentIntentId)),
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
                retrievePaymentIntentUrl(options.baseUrl, requireNotNull(options.paymentIntentOptions?.paymentIntentId)),
                AirwallexHttpRequest.Method.GET
            )
                .addClientSecretHeader(options.clientSecret)
                .build()
        )
    }

    private fun AirwallexHttpRequest.Builder.addClientSecretHeader(
        clientSecret: String
    ): AirwallexHttpRequest.Builder {
        return addHeader(CLIENT_SECRET_HEADER, clientSecret)
    }

    companion object {
        private const val CLIENT_SECRET_HEADER = "client-secret"
        private const val CONTENT_TYPE = "application/json; charset=utf-8"

        /**
         *  `/api/v1/pa/payment_intents/{id}`
         */
        internal fun retrievePaymentIntentUrl(baseUrl: String, paymentIntentId: String): String {
            return getApiUrl(
                baseUrl,
                "payment_intents/%s",
                paymentIntentId
            )
        }

        /**
         *  `/api/v1/pa/payment_intents/{id}/confirm`
         */
        internal fun confirmPaymentIntentUrl(baseUrl: String, paymentIntentId: String): String {
            return getApiUrl(
                baseUrl,
                "payment_intents/%s/confirm",
                paymentIntentId
            )
        }

        private fun getApiUrl(baseUrl: String, path: String, vararg args: Any): String {
            return "$baseUrl/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
        }
    }
}
