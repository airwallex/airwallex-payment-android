package com.airwallex.android

import com.airwallex.android.exception.AirwallexException

internal interface ApiResultCallback<ResultType> {
    fun onSuccess(result: ResultType)

    fun onError(e: AirwallexException)
}
