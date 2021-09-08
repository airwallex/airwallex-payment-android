package com.airwallex.android.core

/**
 * Retrieve SecurityToken listener
 */
interface SecurityTokenListener {

    fun onResponse(deviceId: String)
}
