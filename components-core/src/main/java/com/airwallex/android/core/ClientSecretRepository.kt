package com.airwallex.android.core

import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.ClientSecret
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClientSecretRepository(private val provider: ClientSecretProvider) {

    fun retrieveClientSecret(customerId: String, listener: ClientSecretRetrieveListener) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    provider.provideClientSecret(customerId)
                }
            }
            result.fold(
                onSuccess = { listener.onClientSecretRetrieve(it) },
                onFailure = { listener.onClientSecretError("Could not retrieve client secret from the given provider") }
            )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun retrieveClientSecret(customerId: String): ClientSecret {
        return withContext(Dispatchers.IO) {
            try {
                val clientSecret = provider.provideClientSecret(customerId)
                TokenManager.updateClientSecret(clientSecret.value)
                clientSecret
            } catch (throwable: Throwable) {
                throw AirwallexCheckoutException(
                    message = "Could not retrieve client secret from the given provider", e = throwable
                )
            }
        }
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

        @Throws(AirwallexCheckoutException::class)
        fun getInstance(): ClientSecretRepository {
            return this.instance
                ?: throw AirwallexCheckoutException(
                    message = "Attempted to get instance of ClientSecretManager without initialization."
                )
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
