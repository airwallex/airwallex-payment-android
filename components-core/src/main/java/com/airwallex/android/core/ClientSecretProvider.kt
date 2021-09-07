package com.airwallex.android.core

import com.airwallex.android.core.model.ClientSecret

/**
 * Represents an object that can call to a server and create [ClientSecret]
 */
interface ClientSecretProvider {

    /**
     * When called, talks to a client server that then communicates with Airwallex's servers to create an [ClientSecret].
     *
     * @param customerId Id of the current customer
     */
    fun provideClientSecret(customerId: String): ClientSecret
}
