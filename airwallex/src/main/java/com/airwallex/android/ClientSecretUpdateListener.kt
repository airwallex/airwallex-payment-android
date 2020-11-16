package com.airwallex.android

interface ClientSecretUpdateListener {

    /**
     * Client secret update success
     *
     * @param customerId Id of the current customer
     * @param responseJson the raw JSON String returned from Airwallex's server
     */
    fun onClientSecretUpdate(customerId: String, responseJson: String)

    /**
     * Client secret update failed
     *
     * @param message the error message returned from Airwallex's servers
     */
    fun onClientSecretUpdateFailure(message: String)
}
