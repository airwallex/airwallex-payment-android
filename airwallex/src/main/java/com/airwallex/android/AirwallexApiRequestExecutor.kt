package com.airwallex.android

import com.airwallex.android.exception.APIConnectionException
import java.io.IOException

/**
 * Used by [StripeApiRepository] to make HTTP requests
 */
internal class AirwallexApiRequestExecutor internal constructor(
) : ApiRequestExecutor {
    private val connectionFactory: ConnectionFactory = ConnectionFactory()

    /**
     * Make the request and return the response as a [StripeResponse]
     */
    override fun execute(request: ApiRequest): AirwallexResponse {

        connectionFactory.create(request).use {
            try {
                val stripeResponse = it.response
                return stripeResponse
            } catch (e: IOException) {
                throw APIConnectionException.create(e, request.baseUrl)
            }
        }
    }
}
