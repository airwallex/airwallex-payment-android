package com.airwallex.android.card.exception

import com.airwallex.android.core.exception.AirwallexException

class ThreeDSException(
    message: String
) : AirwallexException(null, null, 0, message)
