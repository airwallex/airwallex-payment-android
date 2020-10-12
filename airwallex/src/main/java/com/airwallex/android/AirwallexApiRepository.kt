package com.airwallex.android

import android.net.Uri
import com.airwallex.android.model.PaymentIntentConfirmRequest
import com.airwallex.android.model.PaymentIntentContinueRequest
import com.airwallex.android.model.PaymentMethodCreateRequest
import com.airwallex.android.model.PaymentMethodType
import com.google.gson.JsonParser
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

/**
 * The implementation of [ApiRepository] to request the Airwallex API.
 */
internal class AirwallexApiRepository : ApiRepository {

    @Parcelize
    internal class RetrievePaymentIntentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentIntentId: String
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class ConfirmPaymentIntentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentIntentId: String,
        internal val request: PaymentIntentConfirmRequest? = null
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class ContinuePaymentIntentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentIntentId: String,
        internal val request: PaymentIntentContinueRequest
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal data class CreatePaymentMethodOptions internal constructor(
        override val clientSecret: String,
        internal val customerId: String,
        internal val request: PaymentMethodCreateRequest
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal data class RetrievePaymentMethodOptions internal constructor(
        override val clientSecret: String,
        internal val customerId: String,
        /**
         * Page number starting from 0
         */
        internal val pageNum: Int,
        /**
         * Number of payment methods to be listed per page
         */
        internal val pageSize: Int,
        /**
         * The start time of created_at in ISO8601 format
         */
        internal val fromCreatedAt: Date? = null,
        /**
         * The end time of created_at in ISO8601 format
         */
        internal val toCreatedAt: Date? = null,
        /**
         * Payment method type
         */
        internal val type: PaymentMethodType
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class RetrievePaResOptions internal constructor(
        override val clientSecret: String,
        internal val paResId: String
    ) : ApiRepository.Options(clientSecret = clientSecret)

    /**
     * Continue a PaymentIntent using the provided [ApiRepository.Options]
     *
     * @param options contains the confirm params
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    override fun continuePaymentIntent(options: ApiRepository.Options): AirwallexHttpResponse? {
        val paramsJson =
            JsonParser().parse(AirwallexPlugins.gson.toJson((options as ContinuePaymentIntentOptions).request))
                .asJsonObject

        val request = AirwallexHttpRequest.Builder(
            continuePaymentIntentUrl(
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
        return AirwallexPlugins.httpClient.execute(request)
    }

    /**
     * Confirm a PaymentIntent using the provided [ApiRepository.Options]
     *
     * @param options contains the confirm params
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    override fun confirmPaymentIntent(options: ApiRepository.Options): AirwallexHttpResponse? {
        val paramsJson =
            JsonParser().parse(AirwallexPlugins.gson.toJson((options as ConfirmPaymentIntentOptions).request))
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
                (options as RetrievePaymentIntentOptions).paymentIntentId
            ),
            AirwallexHttpRequest.Method.GET
        )
            .addClientSecretHeader(options.clientSecret)
            .build()
        return AirwallexPlugins.httpClient.execute(request)
    }

    override fun createPaymentMethod(options: ApiRepository.Options): AirwallexHttpResponse? {
        val paramsJson =
            JsonParser().parse(AirwallexPlugins.gson.toJson(requireNotNull((options as CreatePaymentMethodOptions).request)))
                .asJsonObject

        val request = AirwallexHttpRequest.Builder(
            createPaymentMethodUrl(
                AirwallexPlugins.baseUrl
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
        return AirwallexPlugins.httpClient.execute(request)
    }

    override fun retrievePaymentMethods(options: ApiRepository.Options): AirwallexHttpResponse? {
        val request = AirwallexHttpRequest.Builder(
            retrievePaymentMethodsUrl(
                AirwallexPlugins.baseUrl,
                (options as RetrievePaymentMethodOptions).customerId,
                options.pageNum,
                options.pageSize,
                options.fromCreatedAt,
                options.toCreatedAt,
                options.type
            ),
            AirwallexHttpRequest.Method.GET
        )
            .addClientSecretHeader(options.clientSecret)
            .build()
        return AirwallexPlugins.httpClient.execute(request)
    }

    override fun retrieveParesWithId(options: ApiRepository.Options): AirwallexHttpResponse? {
        val request = AirwallexHttpRequest.Builder(
            Uri.parse(BuildConfig.PARES_CACHE_BASE_URL).buildUpon().appendQueryParameter("paResId", (options as RetrievePaResOptions).paResId).toString(),
            AirwallexHttpRequest.Method.GET
        )
            .build()
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
        internal fun continuePaymentIntentUrl(baseUrl: String, paymentIntentId: String): String {
            return getApiUrl(
                baseUrl,
                "payment_intents/%s/confirm_continue",
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

        /**
         *  `/api/v1/pa/payment_methods/create`
         */
        private fun createPaymentMethodUrl(baseUrl: String): String {
            return getApiUrl(
                baseUrl,
                "payment_methods/create"
            )
        }

        /**
         *  `/api/v1/pa/payment_methods/create`
         */
        private fun retrievePaymentMethodsUrl(
            baseUrl: String,
            customerId: String,
            pageNum: Int,
            pageSize: Int,
            fromCreatedAt: Date?,
            toCreatedAt: Date?,
            type: PaymentMethodType
        ): String {
            val builder = StringBuilder("payment_methods?")
            builder.append("page_num=$pageNum")
            builder.append("&page_size=$pageSize")
            builder.append("&customer_id=$customerId")
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            fromCreatedAt?.let {
                builder.append("&from_created_at=${format.format(fromCreatedAt)}")
            }
            toCreatedAt?.let {
                builder.append("&to_created_at=${format.format(toCreatedAt)}")
            }
            builder.append("&type=${type.value}")
            return getApiUrl(
                baseUrl,
                builder.toString()
            )
        }

        private fun getApiUrl(baseUrl: String, path: String, vararg args: Any): String {
            return "$baseUrl/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
        }
    }
}
