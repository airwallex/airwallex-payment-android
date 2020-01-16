package com.airwallex.android

import androidx.annotation.VisibleForTesting
import com.airwallex.android.exception.APIConnectionException
import com.airwallex.android.model.AirwallexModel
import com.airwallex.android.model.parser.PaymentIntentJsonParser
import java.io.IOException
import java.util.*

internal class AirwallexApiRepository(
    private val stripeApiRequestExecutor: ApiRequestExecutor = AirwallexApiRequestExecutor()
) : ApiRepository {

    override fun confirmPaymentIntent(token: String, paymentIntentId: String): PaymentIntent? {

        return fetchStripeModel(
            ApiRequest.createPost(
                getConfirmPaymentIntentUrl(paymentIntentId),
                ApiRequest.Options(token),
                mapOf("customer" to "cus_123")
            ),
            PaymentIntentJsonParser()
        )
    }

    override fun retrievePaymentIntent(token: String, paymentIntentId: String): PaymentIntent? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getRetrievePaymentIntentUrl(paymentIntentId: String): String {
        return getApiUrl("payment_intents/%s", paymentIntentId)
    }

    /**
     *  `/api/v1/pa/payment_intents/{id}/confirm`
     */
    @VisibleForTesting
    @JvmSynthetic
    internal fun getConfirmPaymentIntentUrl(paymentIntentId: String): String {
        return getApiUrl("payment_intents/%s/confirm", paymentIntentId)
    }

    private fun getApiUrl(path: String, vararg args: Any): String {
        return getApiUrl(String.format(Locale.ENGLISH, path, *args))
    }

    private fun getApiUrl(path: String): String {
        return "${AirwallexPlugins.baseUrl}/api/v1/pa/$path"
    }

    private fun <ModelType : AirwallexModel> fetchStripeModel(
        apiRequest: ApiRequest,
        jsonParser: ModelJsonParser<ModelType>
    ): ModelType? {
        return jsonParser.parse(makeApiRequest(apiRequest).responseJson)
    }

    @VisibleForTesting
    internal fun makeApiRequest(apiRequest: ApiRequest): AirwallexResponse {
        val response = try {
            stripeApiRequestExecutor.execute(apiRequest)
        } catch (ex: IOException) {
            throw APIConnectionException.create(ex, apiRequest.baseUrl)
        }

        if (response.hasErrorCode()) {
//            handleApiError(response)
        }

        return response
    }


}