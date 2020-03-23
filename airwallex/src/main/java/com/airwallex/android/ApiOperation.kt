package com.airwallex.android

import com.airwallex.android.exception.APIConnectionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

internal abstract class ApiOperation<Result>(
    private val coroutineScope: CoroutineScope = CoroutineScope(IO),
    private val callback: ApiResponseCallback<Result>
) {
    internal abstract suspend fun getResult(): Result?

    internal fun execute() {
        coroutineScope.launch {
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
        }
    }
}
