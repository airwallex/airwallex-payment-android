package com.airwallex.android

import com.airwallex.android.exception.AirwallexException

interface ApiResultCallback<ResultType> {
    fun onSuccess(result: ResultType)

    fun onError(e: AirwallexException)
}
