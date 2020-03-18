package com.airwallex.android

import com.airwallex.android.exception.AirwallexException

internal interface ApiResultCallback<Result> {
    fun onSuccess(result: Result)

    fun onError(e: AirwallexException)
}
