package com.airwallex.android.core.exception

import com.airwallex.android.core.model.AirwallexError
import java.net.HttpURLConnection

class PermissionException(
    error: AirwallexError,
    traceId: String? = null
) : AirwallexException(error, traceId, HttpURLConnection.HTTP_FORBIDDEN)
