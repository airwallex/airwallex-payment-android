package com.airwallex.android

import com.airwallex.android.model.ClientSecret

interface ClientSecretUpdateListener {

    fun onClientSecretUpdate(customerId: String, clientSecret: ClientSecret)

    fun onClientSecretUpdateFailure(message: String)
}
