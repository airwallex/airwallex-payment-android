package com.airwallex.paymentacceptance

import android.text.TextUtils
import com.airwallex.android.AirwallexPlugins
import com.airwallex.android.ClientSecretProvider
import com.airwallex.android.ClientSecretUpdateListener
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.parser.AirwallexErrorParser
import com.airwallex.android.model.parser.ClientSecretParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

class ExampleClientSecretProvider : ClientSecretProvider {

    private val api: Api
        get() {
            if (TextUtils.isEmpty(AirwallexPlugins.environment.baseUrl())) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(AirwallexPlugins.environment.baseUrl()).buildRetrofit()
                .create(Api::class.java)
        }

    override fun createClientSecret(
        customerId: String,
        updateListener: ClientSecretUpdateListener
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = runCatching {
                val response = api.authentication(
                    apiKey = Settings.apiKey,
                    clientId = Settings.clientId
                )
                Settings.token = JSONObject(response.string())["token"].toString()
                api.createClientSecret(customerId)
            }
            withContext(Dispatchers.Main) {
                response.fold(
                    onSuccess = {
                        updateListener.onClientSecretUpdate(ClientSecretParser().parse(JSONObject(it.string())))
                    },
                    onFailure = {
                        val errorMessage = if (it is HttpException) {
                            getErrorMessageFromResponse(it)?.message ?: "Failed to generate client secret!"
                        } else {
                            it.message ?: "Failed to generate client secret!"
                        }
                        updateListener.onClientSecretUpdateFailure(errorMessage)
                    }
                )
            }
        }
    }

    private fun getErrorMessageFromResponse(httpException: HttpException): AirwallexError? {
        return try {
            val body = httpException.response()?.errorBody()
            AirwallexErrorParser().parse(JSONObject(body?.string() ?: ""))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
