package com.airwallex.android

import com.airwallex.android.exception.AirwallexException

internal interface ApiResponseCallback<Result> {
    fun onSuccess(result: Result)

    fun onError(e: AirwallexException)
}
