package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.PaymentIntentParams
import com.airwallex.android.model.PaymentMethodParams
import com.google.gson.JsonParser
import kotlinx.android.parcel.Parcelize
import java.util.*

internal class AirwallexApiRepository : ApiRepository {

    companion object {
        internal const val API_HOST = "https://staging-pci-api.airwallex.com"
    }

    @Parcelize
    internal data class Options internal constructor(
        internal val token: String,
        internal val clientSecret: String,
        internal val paymentIntentId: String? = null,
        internal val pageNum: Int = 0,
        internal val pageSize: Int = 10,
        internal val customerId: String? = null
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
                createPaymentMethodUrl(),
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
        return getApiUrl("payment_intents/%s", requireNotNull(options.paymentIntentId))
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}/confirm`
     */
    private fun confirmPaymentIntentUrl(options: Options): String {
        return getApiUrl("payment_intents/%s/confirm", requireNotNull(options.paymentIntentId))
    }

    /**
     *  `/api/v1/pa/payment_methods/create`
     */
    private fun createPaymentMethodUrl(): String {
        return getApiUrl("payment_methods/create")
    }

    /**
     *  `/api/v1/pa/payment_methods/create`
     */
    private fun getPaymentMethodsUrl(options: Options): String {
        val builder = StringBuilder("payment_methods?")
        builder.append("page_num=${options.pageNum}")
        builder.append("&page_size=${options.pageSize}")
        options.customerId?.let {
//            builder.append("&customer_id=$it")
        }
        return getApiUrl(builder.toString())
    }

    private fun getApiUrl(path: String, vararg args: Any): String {
        return "${API_HOST}/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
    }
}