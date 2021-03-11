package com.airwallex.android

import com.airwallex.android.exception.*
import com.airwallex.android.model.*
import com.airwallex.android.model.parser.*
import kotlinx.android.parcel.Parcelize
import java.io.IOException
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

/**
 * The implementation of [ApiRepository] to request the Airwallex API.
 */
internal class AirwallexApiRepository : ApiRepository {

    private val httpClient: AirwallexHttpClient = AirwallexHttpClient()

    @Parcelize
    internal class RetrievePaymentIntentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentIntentId: String
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class ConfirmPaymentIntentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentIntentId: String,
        internal val request: PaymentIntentConfirmRequest
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
        internal val request: PaymentMethodCreateRequest
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal data class DisablePaymentMethodOptions internal constructor(
        override val clientSecret: String,
        internal val paymentMethodId: String,
        internal val request: PaymentMethodDisableRequest
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
        internal val type: AvaliablePaymentMethodType
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class RetrievePaResOptions internal constructor(
        override val clientSecret: String,
        internal val paResId: String
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class CreatePaymentConsentOptions internal constructor(
        override val clientSecret: String,
        internal val request: PaymentConsentCreateRequest
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class VerifyPaymentConsentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentConsentId: String,
        internal val request: PaymentConsentVerifyRequest
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class DisablePaymentConsentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentConsentId: String,
        internal val request: PaymentConsentDisableRequest
    ) : ApiRepository.Options(clientSecret = clientSecret)

    @Parcelize
    internal class RetrievePaymentConsentOptions internal constructor(
        override val clientSecret: String,
        internal val paymentConsentId: String
    ) : ApiRepository.Options(clientSecret = clientSecret)

    /**
     * Continue a PaymentIntent using the provided [ApiRepository.Options]
     *
     * @param options contains the confirm params
     * @return a [PaymentIntent] from Airwallex server
     */
    override fun continuePaymentIntent(options: ApiRepository.Options): PaymentIntent? {
        return executeApiRequest(
            AirwallexHttpRequest.createPost(
                url = continuePaymentIntentUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as ContinuePaymentIntentOptions).paymentIntentId
                ),
                options = options,
                params = options.request.toParamMap()
            ),
            PaymentIntentParser()
        )
    }

    /**
     * Confirm a PaymentIntent using the provided [ApiRepository.Options]
     *
     * @param options contains the confirm params
     * @return a [PaymentIntent] from Airwallex server
     */
    override fun confirmPaymentIntent(options: ApiRepository.Options): PaymentIntent? {
        return executeApiRequest(
            AirwallexHttpRequest.createPost(
                url = confirmPaymentIntentUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as ConfirmPaymentIntentOptions).paymentIntentId
                ),
                options = options,
                params = options.request.toParamMap()
            ),
            PaymentIntentParser()
        )
    }

    /**
     * Retrieve a PaymentIntent using the provided [ApiRepository.Options]
     *
     * @param options contains the retrieve params
     * @return a [PaymentIntent] from Airwallex server
     */
    override fun retrievePaymentIntent(options: ApiRepository.Options): PaymentIntent? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = retrievePaymentIntentUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as RetrievePaymentIntentOptions).paymentIntentId
                ),
                options = options,
                params = null
            ),
            PaymentIntentParser()
        )
    }

    override fun createPaymentMethod(options: ApiRepository.Options): PaymentMethod? {
        return executeApiRequest(
            AirwallexHttpRequest.createPost(
                url = createPaymentMethodUrl(
                    AirwallexPlugins.environment.baseUrl()
                ),
                options = options,
                params = (options as CreatePaymentMethodOptions).request.toParamMap()
            ),
            PaymentMethodParser()
        )
    }

    override fun disablePaymentMethod(options: ApiRepository.Options): PaymentMethod? {
        return executeApiRequest(
            AirwallexHttpRequest.createPost(
                url = disablePaymentMethodUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as DisablePaymentMethodOptions).paymentMethodId
                ),
                options = options,
                params = options.request.toParamMap()
            ),
            PaymentMethodParser()
        )
    }

    override fun retrievePaymentMethods(options: ApiRepository.Options): PaymentMethodResponse? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = retrievePaymentMethodsUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as RetrievePaymentMethodOptions).customerId,
                    options.pageNum,
                    options.pageSize,
                    options.fromCreatedAt,
                    options.toCreatedAt,
                    options.type
                ),
                options = options,
                params = null
            ),
            PaymentMethodResponseParser()
        )
    }

    override fun retrieveParesWithId(options: ApiRepository.Options): ThreeDSecurePares? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = paResRetrieveUrl((options as RetrievePaResOptions).paResId),
                options = options,
                params = null
            ),
            ThreeDSecureParesParser()
        )
    }

    override fun createPaymentConsent(options: ApiRepository.Options): PaymentConsent? {
        return executeApiRequest(
            AirwallexHttpRequest.createPost(
                url = createPaymentConsentUrl(
                    AirwallexPlugins.environment.baseUrl()
                ),
                options = options,
                params = (options as CreatePaymentConsentOptions).request.toParamMap()
            ),
            PaymentConsentParser()
        )
    }

    override fun verifyPaymentConsent(options: ApiRepository.Options): PaymentConsent? {
        return executeApiRequest(
            AirwallexHttpRequest.createPost(
                url = verifyPaymentConsentUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as VerifyPaymentConsentOptions).paymentConsentId,
                ),
                options = options,
                params = options.request.toParamMap()
            ),
            PaymentConsentParser()
        )
    }

    override fun disablePaymentConsent(options: ApiRepository.Options): PaymentConsent? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = disablePaymentConsentUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as DisablePaymentConsentOptions).paymentConsentId
                ),
                options = options,
                params = options.request.toParamMap()
            ),
            PaymentConsentParser()
        )
    }

    override fun retrievePaymentConsent(options: ApiRepository.Options): PaymentConsent? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = retrievePaymentConsentUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as RetrievePaymentConsentOptions).paymentConsentId
                ),
                options = options,
                params = null
            ),
            PaymentConsentParser()
        )
    }

    @Throws(
        AuthenticationException::class,
        InvalidRequestException::class,
        PermissionException::class,
        APIException::class,
        APIConnectionException::class
    )
    private fun <ModelType : AirwallexModel> executeApiRequest(
        request: AirwallexHttpRequest,
        jsonParser: ModelJsonParser<ModelType>
    ): ModelType? {
        val response = runCatching {
            httpClient.execute(request)
        }.getOrElse {
            throw when (it) {
                is IOException -> APIConnectionException.create(it, request.url)
                else -> it
            }
        }

        if (response.isError) {
            handleApiError(response)
        }

        return jsonParser.parse(response.responseJson)
    }

    @Throws(
        AuthenticationException::class,
        InvalidRequestException::class,
        PermissionException::class,
        APIException::class
    )
    private fun handleApiError(response: AirwallexHttpResponse) {
        val traceId = response.traceId
        val responseCode = response.code
        val error = AirwallexErrorParser().parse(response.responseJson)
        when (responseCode) {
            HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_NOT_FOUND -> {
                throw InvalidRequestException(error = error, traceId = traceId)
            }
            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                throw AuthenticationException(error = error, traceId = traceId)
            }
            HttpURLConnection.HTTP_FORBIDDEN -> {
                throw PermissionException(error = error, traceId = traceId)
            }
            else -> {
                throw APIException(error = error, traceId = traceId, statusCode = responseCode)
            }
        }
    }

    companion object {
        /**
         * paRes base url
         */
        private fun retrievePaResBaseUrl(): String {
            return AirwallexPlugins.environment.cybsUrl()
        }

        /**
         * `/paresCache?paResId=%s`
         */
        internal fun paResRetrieveUrl(paResId: String): String {
            return "${retrievePaResBaseUrl()}/${String.format("/paresCache?paResId=%s", paResId)}"
        }

        /**
         * `/pares/callback`
         */
        internal fun paResTermUrl(): String {
            return "${retrievePaResBaseUrl()}/pares/callback"
        }

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
         *  `/api/v1/pa/payment_methods/{id}/disable`
         */
        private fun disablePaymentMethodUrl(baseUrl: String, paymentMethodId: String): String {
            return getApiUrl(
                baseUrl,
                "payment_methods/%s/disable",
                paymentMethodId
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
            type: AvaliablePaymentMethodType
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

        /**
         *  `/api/v1/pa/payment_consents/create`
         */
        internal fun createPaymentConsentUrl(baseUrl: String): String {
            return getApiUrl(
                baseUrl,
                "payment_consents/create"
            )
        }

        /**
         *  `/api/v1/pa/payment_consents/{id}/verify`
         */
        internal fun verifyPaymentConsentUrl(baseUrl: String, paymentConsentId: String): String {
            return getApiUrl(
                baseUrl,
                "payment_consents/%s/verify",
                paymentConsentId
            )
        }

        /**
         *  `/api/v1/pa/payment_consents/{id}/disable`
         */
        internal fun disablePaymentConsentUrl(baseUrl: String, paymentConsentId: String): String {
            return getApiUrl(
                baseUrl,
                "payment_consents/%s/disable",
                paymentConsentId
            )
        }

        /**
         *  `/api/v1/pa/payment_consents/{id}`
         */
        internal fun retrievePaymentConsentUrl(baseUrl: String, paymentConsentId: String): String {
            return getApiUrl(
                baseUrl,
                "payment_consents/%s",
                paymentConsentId
            )
        }

        @Suppress("DEPRECATION")
        private fun getApiUrl(baseUrl: String, path: String, vararg args: Any): String {
            return "$baseUrl/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
        }
    }
}
