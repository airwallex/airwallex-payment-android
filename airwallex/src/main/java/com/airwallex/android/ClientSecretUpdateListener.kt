package com.airwallex.android

import com.airwallex.android.model.ClientSecret

interface ClientSecretUpdateListener {

    /**
     * Client secret update success
     *
     * @param clientSecret the clientSecret returned from Airwallex's server
     */
    fun onClientSecretUpdate(clientSecret: ClientSecret)

    /**
     * Client secret update failed
     *
     * @param message the error message returned from Airwallex's servers
     */
    fun onClientSecretUpdateFailure(message: String)
}
