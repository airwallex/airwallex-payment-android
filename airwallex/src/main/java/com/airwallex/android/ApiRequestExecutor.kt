package com.airwallex.android

internal interface ApiRequestExecutor {
    fun execute(request: ApiRequest): AirwallexResponse
}
