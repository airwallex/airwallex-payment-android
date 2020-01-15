package com.airwallex.android

import androidx.annotation.VisibleForTesting
import com.airwallex.android.exception.APIConnectionException
import java.io.IOException
import java.util.*

internal class AirwallexApiRepository(
    internal val options: ApiRequest.Options,
    private val stripeApiRequestExecutor: ApiRequestExecutor = AirwallexApiRequestExecutor()
) : AirwallexRepository {

    override fun confirmPaymentIntent(
        confirmPaymentIntentParams: ConfirmPaymentIntentParams,
        options: ApiRequest.Options
    ): PaymentIntent? {


//        return fetchStripeModel(
//            ApiRequest.createPost(
//                apiUrl, options, params, appInfo
//            ),
//            PaymentIntentJsonParser()
//        )

//        val apiUrl = getConfirmPaymentIntentUrl(
//            PaymentIntent.ClientSecret(confirmPaymentIntentParams.clientSecret).paymentIntentId
//        )
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrievePaymentIntent(options: ApiRequest.Options): PaymentIntent? {
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
        return "${options.url}/api/v1/pa/$path"
    }

//    private fun <ModelType : StripeModel> fetchStripeModel(
//        apiRequest: ApiRequest,
//        jsonParser: ModelJsonParser<ModelType>
//    ): ModelType? {
//        return jsonParser.parse(makeApiRequest(apiRequest).responseJson)
//    }


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