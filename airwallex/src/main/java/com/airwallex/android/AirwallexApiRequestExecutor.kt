package com.airwallex.android

import com.airwallex.android.exception.APIConnectionException
import java.io.IOException

internal class AirwallexApiRequestExecutor internal constructor(
) : ApiRequestExecutor {
    private val connectionFactory: ConnectionFactory = ConnectionFactory()

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
