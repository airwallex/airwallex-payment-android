package com.airwallex.android

import com.airwallex.android.exception.APIConnectionException
import com.airwallex.android.exception.AirwallexException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

internal abstract class ApiExecutor<Response>(
    private val coroutineScope: CoroutineScope = CoroutineScope(IO),
    private val listener: ApiResponseListener<Response>
) {
    internal abstract suspend fun getResponse(): Response?

    internal fun execute() {
        coroutineScope.launch {
            val responseWrapper: ResponseWrapper<Response> = try {
                ResponseWrapper.create(getResponse())
            } catch (e: IOException) {
                ResponseWrapper.create(APIConnectionException.create(e))
            }

            withContext(Main) {
                dispatchResponse(responseWrapper)
            }
        }
    }

    private fun dispatchResponse(responseWrapper: ResponseWrapper<Response>) {
        when {
            responseWrapper.response != null -> listener.onSuccess(responseWrapper.response)
            responseWrapper.exception != null -> listener.onError(responseWrapper.exception)
        }
    }

    private data class ResponseWrapper<Response> internal constructor(
        val response: Response? = null,
        val exception: AirwallexException? = null
    ) {
        internal companion object {
            @JvmSynthetic
            internal fun <Response> create(response: Response?): ResponseWrapper<Response> {
                return ResponseWrapper(response = response)
            }

            @JvmSynthetic
            internal fun <Response> create(error: AirwallexException): ResponseWrapper<Response> {
                return ResponseWrapper(exception = error)
            }
        }
    }

    internal interface ApiResponseListener<Response> {
        fun onSuccess(response: Response)

        fun onError(e: AirwallexException)
    }
}
