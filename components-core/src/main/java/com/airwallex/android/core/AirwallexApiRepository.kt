package com.airwallex.android.core

import android.net.Uri
import com.airwallex.android.core.exception.*
import com.airwallex.android.core.http.AirwallexHttpClient
import com.airwallex.android.core.http.AirwallexHttpRequest
import com.airwallex.android.core.http.AirwallexHttpResponse
import com.airwallex.android.core.log.Logger
import com.airwallex.android.core.model.*
import com.airwallex.android.core.model.parser.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*

/**
 * The implementation of [ApiRepository] to request the Airwallex API.
 */
class AirwallexApiRepository : ApiRepository {

    private val httpClient: AirwallexHttpClient = AirwallexHttpClient()

    @Parcelize
    class RetrievePaymentIntentOptions(
        override val clientSecret: String,
        val paymentIntentId: String
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class ConfirmPaymentIntentOptions(
        override val clientSecret: String,
        internal val paymentIntentId: String,
        internal val request: PaymentIntentConfirmRequest
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class ContinuePaymentIntentOptions(
        override val clientSecret: String,
        internal val paymentIntentId: String,
        val request: PaymentIntentContinueRequest
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    data class CreatePaymentMethodOptions(
        override val clientSecret: String,
        val request: PaymentMethodCreateRequest
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class CreatePaymentConsentOptions(
        override val clientSecret: String,
        internal val request: PaymentConsentCreateRequest
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class VerifyPaymentConsentOptions(
        override val clientSecret: String,
        internal val paymentConsentId: String,
        internal val request: PaymentConsentVerifyRequest
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class DisablePaymentConsentOptions constructor(
        override val clientSecret: String,
        internal val paymentConsentId: String,
        internal val request: PaymentConsentDisableRequest
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class RetrievePaymentConsentOptions constructor(
        override val clientSecret: String,
        internal val paymentConsentId: String
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class TrackerOptions(
        internal val request: TrackerRequest
    ) : Options(clientSecret = "")

    @Parcelize
    class RetrieveAvailablePaymentMethodsOptions(
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
        internal val transactionMode: TransactionMode?,
        /**
         * The supported country code
         */
        internal val countryCode: String?
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class RetrievePaymentMethodTypeInfoOptions(
        override val clientSecret: String,
        /**
         * bank_transfer, online_banking etc.
         */
        internal val paymentMethodType: String,
        /**
         * webqr, mweb, jsapi, inapp, miniprog
         */
        internal val flow: AirwallexPaymentRequestFlow?,
        /**
         * recurring, oneoff
         */
        internal val transactionMode: TransactionMode?,
        /**
         * Country code
         */
        internal val countryCode: String?,
        /**
         * Open Id
         */
        internal val openId: String?
    ) : Options(clientSecret = clientSecret)

    @Parcelize
    class RetrieveBankOptions(
        override val clientSecret: String,
        /**
         * bank_transfer, online_banking etc.
         */
        internal val paymentMethodType: String,
        /**
         * webqr, mweb, jsapi, inapp, miniprog
         */
        internal val flow: AirwallexPaymentRequestFlow?,
        /**
         * recurring, oneoff
         */
        internal val transactionMode: TransactionMode?,
        /**
         * For payment method like online_banking that supports different bank list in different country, the country code is required.
         * such payment method: online_banking, bank_transfer
         */
        internal val countryCode: String?,
        /**
         * Open Id
         */
        internal val openId: String?
    ) : Options(clientSecret = clientSecret)

    /**
     * Continue a PaymentIntent using the provided [Options]
     *
     * @param options contains the confirm params
     * @return a [PaymentIntent] from Airwallex server
     */
    override suspend fun continuePaymentIntent(options: Options): PaymentIntent? {
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
     * Confirm a PaymentIntent using the provided [Options]
     *
     * @param options contains the confirm params
     * @return a [PaymentIntent] from Airwallex server
     */
    override suspend fun confirmPaymentIntent(options: Options): PaymentIntent? {
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
     * Retrieve a PaymentIntent using the provided [Options]
     *
     * @param options contains the retrieve params
     * @return a [PaymentIntent] from Airwallex server
     */
    override suspend fun retrievePaymentIntent(options: Options): PaymentIntent? {
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

    override suspend fun createPaymentMethod(options: Options): PaymentMethod? {
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

    override suspend fun createPaymentConsent(options: Options): PaymentConsent? {
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

    override suspend fun verifyPaymentConsent(options: Options): PaymentConsent? {
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

    override suspend fun disablePaymentConsent(options: Options): PaymentConsent? {
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

    override suspend fun retrievePaymentConsent(options: Options): PaymentConsent? {
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

    override suspend fun tracker(options: Options) {
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

    override suspend fun executeMockWeChat(mockWeChatUrl: String) {
        runCatching {
            httpClient.execute(
                AirwallexHttpRequest.createGet(
                    url = mockWeChatUrl,
                    options = Options(""),
                    params = null,
                    accept = "*/*;q=0.8"
                )
            )
        }.getOrElse {
            Logger.debug("Execute Mock WeChat failed.")
        }
    }

    override suspend fun retrieveAvailablePaymentMethods(options: Options): AvailablePaymentMethodTypeResponse? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = retrieveAvailablePaymentMethodsUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as RetrieveAvailablePaymentMethodsOptions).pageNum,
                    options.pageSize,
                    options.active,
                    options.transactionCurrency,
                    options.transactionMode,
                    options.countryCode
                ),
                options = options,
                params = null
            ),
            AvailablePaymentMethodTypeResponseParser()
        )
    }

    override suspend fun retrievePaymentMethodTypeInfo(options: Options): PaymentMethodTypeInfo? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = retrievePaymentMethodTypeInfoUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as RetrievePaymentMethodTypeInfoOptions).paymentMethodType,
                    options.countryCode,
                    options.flow,
                    options.openId
                ),
                options = options,
                params = null
            ),
            PaymentMethodTypeInfoParser()
        )
    }

    override suspend fun retrieveBanks(options: Options): BankResponse? {
        return executeApiRequest(
            AirwallexHttpRequest.createGet(
                url = retrieveBanksUrl(
                    AirwallexPlugins.environment.baseUrl(),
                    (options as RetrieveBankOptions).paymentMethodType,
                    options.countryCode,
                    options.flow,
                    options.openId
                ),
                options = options,
                params = null
            ),
            BankResponseParser()
        )
    }

    @Throws(
        AuthenticationException::class,
        InvalidRequestException::class,
        PermissionException::class,
        APIException::class,
        APIConnectionException::class
    )

    private suspend fun <ModelType : AirwallexModel> executeApiRequest(
        request: AirwallexHttpRequest,
        jsonParser: ModelJsonParser<ModelType>
    ): ModelType? = withContext(Dispatchers.IO) {
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

        jsonParser.parse(response.responseJson)
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
        internal fun createPaymentMethodUrl(baseUrl: String): String {
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
            transactionMode: TransactionMode?,
            countryCode: String?
        ): String {
            val url = getApiUrl(
                baseUrl,
                "config/payment_method_types"
            )

            val builder = Uri.parse(url).buildUpon()
            builder.appendQueryParameter("__resources", "true")
            builder.appendQueryParameter("os_type", "android")
            builder.appendQueryParameter("lang", "en")
            pageNum?.let {
                builder.appendQueryParameter("page_num", it.toString())
            }
            pageSize?.let {
                builder.appendQueryParameter("page_size", it.toString())
            }
            active?.let {
                builder.appendQueryParameter("active", it.toString())
            }
            transactionCurrency?.let {
                builder.appendQueryParameter("transaction_currency", it)
            }
            transactionMode?.let {
                builder.appendQueryParameter("transaction_mode", it.value)
            }
            countryCode?.let {
                builder.appendQueryParameter("country_code", it)
            }
            return builder.build().toString()
        }

        /**
         * `/api/v1/pa/config/payment_method_types/{payment_method_type}?flow={flow}`
         */
        internal fun retrievePaymentMethodTypeInfoUrl(
            baseUrl: String,
            paymentMethodType: String,
            countryCode: String?,
            flow: AirwallexPaymentRequestFlow?,
            openId: String?
        ): String {
            val url = getApiUrl(
                baseUrl,
                "config/payment_method_types/%s",
                paymentMethodType
            )

            val builder = Uri.parse(url).buildUpon()
            countryCode?.let {
                builder.appendQueryParameter("country_code", it)
            }
            flow?.let {
                builder.appendQueryParameter("flow", it.value)
            }
            openId?.let {
                builder.appendQueryParameter("open_id", it)
            }
            builder.appendQueryParameter("os_type", "android")
            builder.appendQueryParameter("lang", "en")
            return builder.build().toString()
        }

        /**
         * `/api/v1/pa/config/banks?payment_method_type={payment_method_type}&country_code={TH}&lang={zh}`
         */
        internal fun retrieveBanksUrl(
            baseUrl: String,
            paymentMethodType: String,
            countryCode: String?,
            flow: AirwallexPaymentRequestFlow?,
            openId: String?
        ): String {
            val url = getApiUrl(
                baseUrl,
                "config/banks"
            )
            val builder = Uri.parse(url).buildUpon()
            builder.appendQueryParameter("payment_method_type", paymentMethodType)
            builder.appendQueryParameter("__all_logos", "true")
            countryCode?.let {
                builder.appendQueryParameter("country_code", it)
            }
            flow?.let {
                builder.appendQueryParameter("flow", it.value)
            }
            openId?.let {
                builder.appendQueryParameter("open_id", it)
            }
            builder.appendQueryParameter("os_type", "android")
            builder.appendQueryParameter("lang", "en")
            return builder.build().toString()
        }

        @Suppress("DEPRECATION")
        internal fun getApiUrl(baseUrl: String, path: String, vararg args: Any): String {
            return "$baseUrl/api/v1/pa/${String.format(Locale.ENGLISH, path, *args)}"
        }
    }
}
