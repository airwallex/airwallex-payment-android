package com.airwallex.android.card.exception

import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.AirwallexError

class WebViewConnectionException @JvmOverloads constructor(
    message: String,
    e: Throwable? = null
) : AirwallexException(AirwallexError(message = message), null, 0, message, e)
