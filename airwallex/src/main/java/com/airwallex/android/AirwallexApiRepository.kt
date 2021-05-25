package com.airwallex.android

import android.net.Uri
import com.airwallex.android.exception.*
import com.airwallex.android.model.*
import com.airwallex.android.model.parser.*
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
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

    @Parcelize
    internal class TrackerOptions internal constructor(
        internal val request: TrackerRequest
    ) : ApiRepository.Options(clientSecret = "")

    @Parcelize
    internal class RetrieveAvailablePaymentMethodsOptions internal constructor(
        override val clientSecret: String,
        /**
         * Page number starting from 0
         */
        internal val pageNum: Int,
        /**
         * Number of payment methods to be listed per page
         */
        internal val pageSize: Int,
        /**
         * Indicate whether the payment method type is active
         */
        internal val active: Boolean?,
        /**
         * The supported transaction currency
         */
        internal val transactionCurrency: String?,
        /**
         * The supported transaction mode. One of oneoff, recurring.
         */
        internal val transactionMode: String?,
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
            AirwallexHttpRequest.createPost(
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
                params = null,
                awxTracker = UUID.randomUUID().toString()
            ),
            PaymentConsentParser()
        )
    }

    override fun tracker(options: ApiRepository.Options) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val params = (options as TrackerOptions).request.toParamMap()
                val builder = Uri.parse(trackerUrl()).buildUpon()
                params.forEach {
                    builder.appendQueryParameter(it.key, it.value.toString())
                }
                val uri = builder.build()
                httpClient.execute(
                    AirwallexHttpRequest.createGet(
                        url = uri.toString(),
                        options = options,
                        params = null
                    )
                )
            }.getOrElse {
                Logger.debug("Tracker failed.")
            }
        }
    }

    override fun retrieveAvailablePaymentMethods(options: ApiRepository.Options): AvailablePaymentMethodResponse? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = retrieveAvailablePaymentMethodsUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as RetrieveAvailablePaymentMethodsOptions).pageNum,
                    options.pageSize,
                    options.active,
                    options.transactionCurrency,
                    options.transactionMode,
                ),
                options = options,
                params = null
            ),
            AvailablePaymentMethodResponseParser()
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
         *  `/api/v1/checkout/collect`
         */
        internal fun trackerUrl(): String {
            return "${AirwallexPlugins.environment.trackerUrl()}/collect"
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

        /**
         *  `/api/v1/pa/config/payment_method_types`
         */
        internal fun retrieveAvailablePaymentMethodsUrl(
            baseUrl: String,
            pageNum: Int?,
            pageSize: Int?,
            active: Boolean?,
            transactionCurrency: String?,
            transactionMode: String?
        ): String {
            val builder = StringBuilder("config/payment_method_types?")
            pageNum?.let {
                builder.append("&page_num=$it")
            }
            pageSize?.let {
                builder.append("&page_size=$it")
            }
            active?.let {
                builder.append("&active=$it")
            }
            transactionCurrency?.let {
                builder.append("&transaction_currency=$it")
            }
            transactionMode?.let {
                builder.append("&transaction_mode=$it")
            }
            return getApiUrl(
                baseUrl,
                builder.toString()
            )
        }

        @Suppress("DEPRECATION")
        private fun getApiUrl(baseUrl: String, path: String, vararg args: Any): String {
            return "$baseUrl/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
        }
    }
}
