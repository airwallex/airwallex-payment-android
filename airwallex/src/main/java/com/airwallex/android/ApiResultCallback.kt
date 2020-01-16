package com.airwallex.android

interface ApiResultCallback<ResultType> {
    fun onSuccess(result: ResultType)

    fun onError(e: Exception)
}
