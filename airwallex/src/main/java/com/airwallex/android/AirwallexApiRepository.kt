package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.PaymentIntentParams
import com.airwallex.android.model.PaymentMethodParams
import com.google.gson.JsonParser
import java.util.*
import kotlinx.android.parcel.Parcelize

internal class AirwallexApiRepository : ApiRepository {

    // TODO Token should be removed after server changed
    @Parcelize
    internal data class Options internal constructor(
        internal val token: String,
        internal val clientSecret: String,
        internal val baseUrl: String,
        internal val paymentIntentOptions: PaymentIntentOptions? = null,
        internal val paymentMethodOptions: PaymentMethodOptions? = null
    ) : Parcelable

    @Parcelize
    internal data class PaymentIntentOptions internal constructor(
        internal val paymentIntentId: String
    ) : Parcelable

    @Parcelize
    internal data class PaymentMethodOptions internal constructor(
        internal val pageNum: Int = 0,
        internal val pageSize: Int = 20,
        internal val customerId: String
    ) : Parcelable

    override fun confirmPaymentIntent(
        options: Options,
        paymentIntentParams: PaymentIntentParams
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
                        "application/json; charset=utf-8",
                        paramsJson.toString()
                    )
                )
                .addHeader("client-secret", options.clientSecret)
                .build()
        )
    }

    override fun retrievePaymentIntent(options: Options): AirwallexHttpResponse? {
        return AirwallexPlugins.restClient.execute(
            AirwallexHttpRequest.Builder(
                retrievePaymentIntentUrl(options),
                AirwallexHttpRequest.Method.GET
            )
                .addHeader("Authorization", "Bearer ${options.token}")
                .build()
        )
    }

    override fun createPaymentMethod(
        options: Options,
        paymentMethodParams: PaymentMethodParams
    ): AirwallexHttpResponse? {
        val jsonParser = JsonParser()
        val paramsJson =
            jsonParser.parse(AirwallexPlugins.gson.toJson(paymentMethodParams)).asJsonObject

        return AirwallexPlugins.restClient.execute(
            AirwallexHttpRequest.Builder(
                createPaymentMethodUrl(options),
                AirwallexHttpRequest.Method.POST
            )
                .setBody(
                    AirwallexHttpBody(
                        "application/json; charset=utf-8",
                        paramsJson.toString()
                    )
                )
                .addHeader("Authorization", "Bearer ${options.token}")
                .build()
        )
    }

    override fun getPaymentMethods(options: Options): AirwallexHttpResponse? {
        return AirwallexPlugins.restClient.execute(
            AirwallexHttpRequest.Builder(
                getPaymentMethodsUrl(options),
                AirwallexHttpRequest.Method.GET
            )
                .addHeader("Authorization", "Bearer ${options.token}")
                .build()
        )
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

    /**
     *  `/api/v1/pa/payment_methods/create`
     */
    private fun createPaymentMethodUrl(options: Options): String {
        return getApiUrl(
            options.baseUrl,
            "payment_methods/create"
        )
    }

    /**
     *  `/api/v1/pa/payment_methods/create`
     */
    private fun getPaymentMethodsUrl(options: Options): String {
        val builder = StringBuilder("payment_methods?")
        options.paymentMethodOptions?.apply {
            builder.append("page_num=$pageNum")
            builder.append("&page_size=$pageSize")
            builder.append("&customer_id=$customerId")
        }
        return getApiUrl(
            options.baseUrl,
            builder.toString()
        )
    }

    private fun getApiUrl(baseUrl: String, path: String, vararg args: Any): String {
        return "$baseUrl/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
    }
}
