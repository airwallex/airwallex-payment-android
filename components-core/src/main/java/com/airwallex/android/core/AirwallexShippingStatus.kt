package com.airwallex.android.core

import com.airwallex.android.core.model.Shipping

sealed class AirwallexShippingStatus {
    data class Success(val shipping: Shipping) : AirwallexShippingStatus()

    object Cancel : AirwallexShippingStatus()
}
