package com.airwallex.android

import com.airwallex.android.exception.APIConnectionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

internal abstract class ApiExecutor<Response>(
    private val coroutineScope: CoroutineScope = CoroutineScope(IO),
    private val responseCallback: ApiResponseCallback<Response>
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
            responseWrapper.response != null -> responseCallback.onSuccess(responseWrapper.response)
            responseWrapper.exception != null -> responseCallback.onError(responseWrapper.exception)
        }
    }
}
