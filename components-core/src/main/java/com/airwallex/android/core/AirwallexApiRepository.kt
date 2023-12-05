package com.airwallex.android.core

import android.net.Uri
import com.airwallex.android.core.exception.*
import com.airwallex.android.core.http.AirwallexHttpClient
import com.airwallex.android.core.http.AirwallexHttpRequest
import com.airwallex.android.core.http.AirwallexHttpResponse
import com.airwallex.android.core.log.ConsoleLogger
import com.airwallex.android.core.model.*
import com.airwallex.android.core.model.parser.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*

/**
 * The implementation of [ApiRepository] to request the Airwallex API.
 */
class AirwallexApiRepository : ApiRepository {

    private val httpClient: AirwallexHttpClient = AirwallexHttpClient()

    /**
     * Continue a PaymentIntent using the provided [Options]
     *
     * @param options contains the confirm params
     * @return a [PaymentIntent] from Airwallex server
     */
    override suspend fun continuePaymentIntent(options: Options.ContinuePaymentIntentOptions): PaymentIntent? {
        return options.executeApiRequest(PaymentIntentParser())
    }

    /**
     * Confirm a PaymentIntent using the provided [Options]
     *
     * @param options contains the confirm params
     * @return a [PaymentIntent] from Airwallex server
     */
    override suspend fun confirmPaymentIntent(options: Options.ConfirmPaymentIntentOptions): PaymentIntent? {
        return options.executeApiRequest(PaymentIntentParser())
    }

    /**
     * Retrieve a PaymentIntent using the provided [Options]
     *
     * @param options contains the retrieve params
     * @return a [PaymentIntent] from Airwallex server
     */
    override suspend fun retrievePaymentIntent(options: Options.RetrievePaymentIntentOptions): PaymentIntent? {
        return options.executeApiRequest(PaymentIntentParser())
    }

    override suspend fun createPaymentMethod(options: Options.CreatePaymentMethodOptions): PaymentMethod? {
        return options.executeApiRequest(PaymentMethodParser())
    }

    override suspend fun createPaymentConsent(options: Options.CreatePaymentConsentOptions): PaymentConsent? {
        return options.executeApiRequest(PaymentConsentParser())
    }

    override suspend fun verifyPaymentConsent(options: Options.VerifyPaymentConsentOptions): PaymentConsent? {
        return options.executeApiRequest(PaymentConsentParser())
    }

    override suspend fun disablePaymentConsent(options: Options.DisablePaymentConsentOptions): PaymentConsent? {
        return options.executeApiRequest(PaymentConsentParser())
    }

    override suspend fun retrievePaymentConsent(options: Options.RetrievePaymentConsentOptions): PaymentConsent? {
        return options.executeApiRequest(PaymentConsentParser())
    }

    override suspend fun retrieveAvailablePaymentConsents(options: Options.RetrieveAvailablePaymentConsentsOptions): Page<PaymentConsent>? {
        return options.executeApiRequest(PageParser(PaymentConsentParser()))
    }

    override suspend fun tracker(options: Options.TrackerOptions) {
        runCatching {
            httpClient.execute(options.toAirwallexHttpRequest())
        }.getOrElse {
            ConsoleLogger.debug("Tracker failed.")
        }
    }

    override suspend fun executeMockWeChat(mockWeChatUrl: String) {
        runCatching {
            httpClient.execute(
                AirwallexHttpRequest.createGet(
                    url = mockWeChatUrl,
                    options = null,
                    params = null,
                    accept = "*/*;q=0.8"
                )
            )
        }.getOrElse {
            ConsoleLogger.debug("Execute Mock WeChat failed.")
        }
    }

    override suspend fun retrieveAvailablePaymentMethods(options: Options.RetrieveAvailablePaymentMethodsOptions): Page<AvailablePaymentMethodType>? {
        return options.executeApiRequest(PageParser(AvailablePaymentMethodTypeParser()))
    }

    override suspend fun retrievePaymentMethodTypeInfo(options: Options.RetrievePaymentMethodTypeInfoOptions): PaymentMethodTypeInfo? {
        return options.executeApiRequest(PaymentMethodTypeInfoParser())
    }

    override suspend fun retrieveBanks(options: Options.RetrieveBankOptions): BankResponse? {
        return options.executeApiRequest(BankResponseParser())
    }

    @Throws(
        AuthenticationException::class,
        InvalidRequestException::class,
        PermissionException::class,
        APIException::class,
        APIConnectionException::class
    )

    private suspend fun <ModelType : AirwallexModel> Options.executeApiRequest(
        jsonParser: ModelJsonParser<ModelType>
    ): ModelType? = withContext(Dispatchers.IO) {
        val response = runCatching {
            httpClient.execute(toAirwallexHttpRequest())
        }.getOrElse {
            throw when (it) {
                is IOException -> APIConnectionException.create(it, toAirwallexHttpRequest().url)
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
         *  `/api/v1/pa/payment_consents`
         */
        internal fun retrieveAvailablePaymentConsentsUrl(
            baseUrl: String,
            merchantTriggerReason: PaymentConsent.MerchantTriggerReason?,
            nextTriggeredBy: PaymentConsent.NextTriggeredBy?,
            status: PaymentConsent.PaymentConsentStatus?,
            pageNum: Int?,
            pageSize: Int?
        ): String {
            val url = getApiUrl(
                baseUrl,
                "payment_consents"
            )

            val builder = Uri.parse(url).buildUpon()
            merchantTriggerReason?.let {
                builder.appendQueryParameter("merchant_trigger_reason", it.value)
            }
            nextTriggeredBy?.let {
                builder.appendQueryParameter("next_triggered_by", it.value)
            }
            status?.let {
                builder.appendQueryParameter("status", it.value)
            }
            pageNum?.let {
                builder.appendQueryParameter("page_num", it.toString())
            }
            pageSize?.let {
                builder.appendQueryParameter("page_size", it.toString())
            }
            return builder.build().toString()
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
