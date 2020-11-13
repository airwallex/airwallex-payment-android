package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError
import java.net.HttpURLConnection

class AuthenticationException(
    error: AirwallexError,
    traceId: String? = null
) : AirwallexException(error, traceId, HttpURLConnection.HTTP_UNAUTHORIZED)
