package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

class WebViewConnectionException @JvmOverloads constructor(
    message: String,
    e: Throwable? = null
) : AirwallexException(AirwallexError(message = message), null, 0, message, e)
