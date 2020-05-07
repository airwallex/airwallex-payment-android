package com.airwallex.android

import com.airwallex.android.model.AirwallexError
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse

internal interface ThreeDSecureCallback {
    fun onSuccess(validateResponse: ValidateResponse)
    fun onFailed(exception: AirwallexError)
}
