package com.airwallex.android.core

import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.Shipping

sealed class AirwallexShippingStatus {
    data class Success(val shipping: Shipping) : AirwallexShippingStatus()
    // shipping failure
    data class Failure(val exception: AirwallexException) : AirwallexShippingStatus()

    object Cancel : AirwallexShippingStatus()
}
