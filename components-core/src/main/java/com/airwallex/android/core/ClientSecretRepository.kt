package com.airwallex.android.core

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
