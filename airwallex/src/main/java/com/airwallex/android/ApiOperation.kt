package com.airwallex.android

import com.airwallex.android.exception.APIConnectionException
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal abstract class ApiOperation<Result>(
    private val workScope: CoroutineScope = CoroutineScope(IO),
    private val callback: ApiResponseCallback<Result>
) {
    internal abstract suspend fun getResult(): Result?

    internal fun execute() {
        workScope.launch {
            val resultWrapper: ResultWrapper<Result> = try {
                ResultWrapper.create(getResult())
            } catch (e: IOException) {
                ResultWrapper.create(APIConnectionException.create(e))
            }

            withContext(Main) {
                dispatchResult(resultWrapper)
            }
        }
    }

    private fun dispatchResult(resultWrapper: ResultWrapper<Result>) {
        when {
            resultWrapper.result != null -> callback.onSuccess(resultWrapper.result)
            resultWrapper.exception != null -> callback.onError(resultWrapper.exception)
            else -> callback.onError(
                APIConnectionException(
                    message = "The API operation returned neither a result or exception",
                    e = null
                )
            )
        }
    }
}
