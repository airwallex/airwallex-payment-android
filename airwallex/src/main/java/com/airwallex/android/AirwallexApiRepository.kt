package com.airwallex.android

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
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
        internal val paymentIntentId: String? = null
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
                getConfirmPaymentIntentUrl(options),
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
                getRetrievePaymentIntentUrl(options),
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
                getCreateMethodUrl(),
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

    /**
     *  `/api/v1/pa/payment_intents/{id}`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getRetrievePaymentIntentUrl(options: Options): String {
        return getApiUrl("payment_intents/%s", options.paymentIntentId!!)
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}/confirm`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getConfirmPaymentIntentUrl(options: Options): String {
        return getApiUrl("payment_intents/%s/confirm", options.paymentIntentId!!)
    }

    /**
     *  `/api/v1/pa/payment_methods/create`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getCreateMethodUrl(): String {
        return getApiUrl("payment_methods/create")
    }

    private fun getApiUrl(path: String, vararg args: Any): String {
        return "${API_HOST}/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
    }
}