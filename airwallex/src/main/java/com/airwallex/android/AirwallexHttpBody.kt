package com.airwallex.android

/**
 * A data class that contains http request body details
 */
internal data class AirwallexHttpBody(
    val contentType: String,
    val content: String
)
