package com.airwallex.android

import com.airwallex.android.model.ClientSecret

/**
 * Represents an object that can call to a server and create [ClientSecret]
 */
interface ClientSecretProvider {

    /**
     * When called, talks to a client server that then communicates with Airwallex's servers to create an [ClientSecret].
     *
     * @param updateListener a callback object to notify about results
     */
    fun createClientSecret(updateListener: ClientSecretUpdateListener)
}
