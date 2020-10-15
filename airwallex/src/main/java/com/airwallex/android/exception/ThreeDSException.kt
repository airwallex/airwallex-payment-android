package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

class ThreeDSException(
    error: AirwallexError
) : AirwallexException(error, null, 0)
