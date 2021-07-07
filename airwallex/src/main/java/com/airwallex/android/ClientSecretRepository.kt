package com.airwallex.android

import com.airwallex.android.model.ClientSecret

class ClientSecretRepository(
    private val clientSecretProvider: ClientSecretProvider
) : ClientSecretUpdateListener {
    private var clientSecretRetrieveListener: ClientSecretRetrieveListener? = null

    fun retrieveClientSecret(customerId: String, listener: ClientSecretRetrieveListener) {
        clientSecretRetrieveListener = listener
        clientSecretProvider.createClientSecret(customerId, this)
    }

    override fun onClientSecretUpdate(clientSecret: ClientSecret) {
        try {
            clientSecretRetrieveListener?.onClientSecretRetrieve(clientSecret)
        } catch (e: Exception) {
            clientSecretRetrieveListener?.onClientSecretError("Exception while parsing responseJson to ClientSecret. ")
        }
    }

    override fun onClientSecretUpdateFailure(message: String) {
        clientSecretRetrieveListener?.onClientSecretError(message)
    }

    interface ClientSecretRetrieveListener {
        fun onClientSecretRetrieve(
            clientSecret: ClientSecret
        )

        fun onClientSecretError(
            errorMessage: String
        )
    }

    companion object {
        @Volatile
        private var instance: ClientSecretRepository? = null

        fun getInstance(): ClientSecretRepository {
            return checkNotNull(instance) {
                "Attempted to get instance of ClientSecretManager without initialization."
            }
        }

        fun init(clientSecretProvider: ClientSecretProvider) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ClientSecretRepository(clientSecretProvider)
                    }
                }
            }
        }
    }
}
