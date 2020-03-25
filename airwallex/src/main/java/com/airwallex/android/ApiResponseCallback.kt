package com.airwallex.android

import com.airwallex.android.exception.AirwallexException

internal interface ApiResponseCallback<Response> {
    fun onSuccess(response: Response)

    fun onError(e: AirwallexException)
}
