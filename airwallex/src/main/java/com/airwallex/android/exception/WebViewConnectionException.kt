package com.airwallex.android.exception

class WebViewConnectionException @JvmOverloads constructor(
    message: String?,
    e: Throwable? = null
) : AirwallexException(null, message, null, 0, e)
