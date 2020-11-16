package com.airwallex.android

import com.airwallex.android.model.ClientSecret
import com.airwallex.android.model.parser.ClientSecretParser
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

internal class ClientSecretRepository(
    private val clientSecretProvider: ClientSecretProvider
) : ClientSecretUpdateListener {
    private var clientSecretRetrieveListener: ClientSecretRetrieveListener? = null

    private var clientSecret: ClientSecret? = null
    private var customerId: String? = null

    fun retrieveClientSecret(customerId: String, listener: ClientSecretRetrieveListener) {
        val clientSecret = clientSecret
        clientSecretRetrieveListener = listener
        if (clientSecret == null || shouldRefreshKey(clientSecret) || this.customerId != customerId) {
            Logger.debug("Merchant need to create ClientSecret")
            clientSecretProvider.createClientSecret(customerId, this)
        } else {
            Logger.debug("ClientSecret not expiry, just use the last one")
            listener.onClientSecretRetrieve(clientSecret)
        }
    }

    private fun shouldRefreshKey(clientSecret: ClientSecret): Boolean {
        val now = Calendar.getInstance()
        val nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now.timeInMillis)
        val nowPlusBuffer = nowInSeconds + CLIENT_SECRET_REFRESH_BUFFER_IN_SECONDS
        return TimeUnit.MILLISECONDS.toSeconds(clientSecret.expiredTime.time) < nowPlusBuffer
    }

    override fun onClientSecretUpdate(customerId: String, responseJson: String) {
        try {
            val clientSecret = ClientSecretParser().parse(JSONObject(responseJson))
            this.clientSecret = clientSecret
            this.customerId = customerId
            clientSecretRetrieveListener?.onClientSecretRetrieve(clientSecret)
        } catch (e: Exception) {
            clientSecretRetrieveListener?.onClientSecretError("Exception while parsing responseJson to ClientSecret. ")
        }
    }

    override fun onClientSecretUpdateFailure(message: String) {
        clientSecretRetrieveListener?.onClientSecretError(message)
    }

    internal interface ClientSecretRetrieveListener {
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

        private const val CLIENT_SECRET_REFRESH_BUFFER_IN_SECONDS = 60L

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
