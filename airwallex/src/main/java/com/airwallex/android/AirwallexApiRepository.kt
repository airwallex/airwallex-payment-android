package com.airwallex.android

import com.airwallex.android.model.PaymentIntentConfirmRequest
import com.google.gson.JsonParser
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * The implementation of [ApiRepository] to request the Airwallex API.
 */
internal class AirwallexApiRepository : ApiRepository {

    @Parcelize
    internal class PaymentIntentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentIntentId: String,
        internal val paymentIntentConfirmRequest: PaymentIntentConfirmRequest? = null
    ) : ApiRepository.Options(clientSecret = clientSecret)

    /**
     * Confirm a PaymentIntent using the provided [ApiRepository.Options]
     *
     * @param options contains the confirm params
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    override fun confirmPaymentIntent(options: ApiRepository.Options): AirwallexHttpResponse? {
        val paramsJson =
            JsonParser().parse(AirwallexPlugins.gson.toJson(requireNotNull((options as PaymentIntentOptions).paymentIntentConfirmRequest)))
                .asJsonObject

        val request = AirwallexHttpRequest.Builder(
            confirmPaymentIntentUrl(
                AirwallexPlugins.baseUrl,
                options.paymentIntentId
            ),
            AirwallexHttpRequest.Method.POST
        )
            .setBody(
                AirwallexHttpRequest.AirwallexHttpRequestBody(
                    CONTENT_TYPE,
                    paramsJson.toString()
                )
            )
            .addClientSecretHeader(options.clientSecret)
            .build()
        Logger.debug("Confirm PaymentIntent Request: $request")
        return AirwallexPlugins.httpClient.execute(request)
    }

    /**
     * Retrieve a PaymentIntent using the provided [ApiRepository.Options]
     *
     * @param options contains the retrieve params
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    override fun retrievePaymentIntent(options: ApiRepository.Options): AirwallexHttpResponse? {
        val request = AirwallexHttpRequest.Builder(
            retrievePaymentIntentUrl(
                AirwallexPlugins.baseUrl,
                (options as PaymentIntentOptions).paymentIntentId
            ),
            AirwallexHttpRequest.Method.GET
        )
            .addClientSecretHeader(options.clientSecret)
            .build()
        Logger.debug("Retrieve PaymentIntent Request: $request")
        return AirwallexPlugins.httpClient.execute(request)
    }

    /**
     * Extension to add `clientSecret`
     */
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
